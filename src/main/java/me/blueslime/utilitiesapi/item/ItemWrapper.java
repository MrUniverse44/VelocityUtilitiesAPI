package me.blueslime.utilitiesapi.item;

import me.blueslime.utilitiesapi.exceptions.EnchantmentException;
import me.blueslime.utilitiesapi.item.dynamic.DynamicItem;
import me.blueslime.utilitiesapi.item.dynamic.executor.DefaultExecutor;
import me.blueslime.utilitiesapi.item.dynamic.executor.DynamicExecutor;
import me.blueslime.utilitiesapi.item.nbt.ItemNBT;
import me.blueslime.utilitiesapi.text.TextUtilities;
import me.blueslime.utilitiesapi.tools.PluginTools;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "deprecation"})
public class ItemWrapper implements Cloneable {

    private static boolean ENCHANTMENT_WARNING = false;
    private DynamicExecutor executor = new DefaultExecutor();
    private ItemStack item;

    private ItemWrapper(String material, int amount, String name, List<String> lore, List<String> enchantments, String... nbts) {
        item = parseItemStack(material);
        setName(name);
        setLore(lore);
        setAmount(amount);
        setEnchantments(enchantments);
        addStringNBT(nbts);
    }

    private ItemWrapper(ItemStack item) {
        this.item = item.clone();
    }

    public ItemWrapper setDynamic(DynamicExecutor executor) {
        this.executor = executor;
        return this;
    }

    public void setName(String name) {
        checkItem();

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        if (name != null) {
            meta.setDisplayName(
                    TextUtilities.colorize(name)
            );
        } else {
            meta.setDisplayName(null);
        }

        item.setItemMeta(meta);
    }

    public void setLore(List<String> lore) {
        checkItem();

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.setLore(
                TextUtilities.colorizeList(lore)
        );

        item.setItemMeta(meta);
    }

    public void setEnchantments(List<String> enchantments) {
        checkItem();

        Logger logger = null;

        for (String line : enchantments) {
            String[] split = line.replace(" ", "").split(",", 2);

            String name = split[0].toLowerCase(Locale.ENGLISH);

            int level = isNumber(split[1]) ?
                    Integer.parseInt(split[1]) :
                    1;

            Enchantment enchantment = Enchantment.getByName(name);

            if (enchantment != null) {
                item.addUnsafeEnchantment(
                        enchantment,
                        level
                );
                continue;
            }

            if (!ENCHANTMENT_WARNING) {
                ENCHANTMENT_WARNING = true;

                EnchantmentException.build(
                        "Enchantment '" + name + "' was not found, list: " +
                                Arrays.stream(Enchantment.values()).map(
                                        Enchantment::getName
                                ).collect(
                                        Collectors.joining(", ")
                                )
                ).printStackTrace();
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemWrapper addStringNBT(String... nbts) {
        checkItem();

        if (nbts != null) {
            for (String nbt : nbts) {
                String[] split = nbt.split(",", 2);

                String value = split[1];
                String key = split[0];

                item = ItemNBT.addString(
                        item,
                        key,
                        value
                );
            }
        }
        return this;
    }

    /**
     * Creates a clone of the ItemWrapper, in this clon you can edit
     * a custom name, lore or item properties, this includes support for
     * {@link org.bukkit.entity.Player} to include Placeholders or conditions
     *
     * @param player of the used method
     * @return the modified ItemWrapper for this player.
     */
    public ItemWrapper getDynamicItem(Player player) {
        return executor.build(
                DynamicItem.build(
                        clone(),
                        player
                )
        );
    }

    /**
     * Changes the amount of the item
     * @param amount int
     */
    public void setAmount(int amount) {
        checkItem();

        item.setAmount(amount);
    }

    private ItemStack parseItemStack(String value) {
        if (value.contains(":")) {
            String[] split = value.split(":", 2);
            String material = split[0];

            if (isNumber(split[1])) {
                int damage = Integer.parseInt(split[1]);

                return new ItemStack(
                        parseMaterial(material),
                        1,
                        (short) damage
                );
            }
            return new ItemStack(
                    parseMaterial(
                            material
                    )
            );
        }
        return new ItemStack(
                parseMaterial(value)
        );
    }

    private Material parseMaterial(String material) {
        try {
            return Material.valueOf(
                    material.toUpperCase(Locale.ENGLISH)
            );
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return Material.POTION;
        }
    }

    private boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ItemWrapper clone() {
        if (item != null) {
            return new ItemWrapper(
                    item
            );
        }
        return new ItemWrapper(
                parseItemStack("POTION")
        );
    }

    /**
     * Get the display name of the item
     * @return Display name, if the name is null it will return an Empty String ""
     */
    public String getName() {
        if (item != null && item.getItemMeta() != null) {
            return item.getItemMeta().getDisplayName();
        }
        return "";
    }

    /**
     * Get the lore of the item
     * @return lore, if the lore is not added or the item is not set, it will return a Empty List
     */
    public List<String> getLore() {
        if (item != null && item.getItemMeta() != null && item.getItemMeta().hasLore()) {
            return item.getItemMeta().getLore();
        }
        return Collections.emptyList();
    }


    /**
     * Get the item amount
     * @return Integer
     */
    public int getAmount() {
        if (item != null) {
            return item.getAmount();
        }
        return 1;
    }

    private void checkItem() {
        if (item == null) {
            item = parseItemStack("POTION");
        }
    }

    public ItemStack getItem() {
        return item;
    }

    /**
     * Load a new item
     * @param material for the ItemStack
     * @param amount of the ItemStack
     * @param name of the ItemStack
     * @param lore of the ItemStack
     * @param enchantments for the ItemStack
     * @param nbts for the ItemStack
     * @return a ItemWrapper
     */
    public static ItemWrapper fromData(String material, int amount, String name, List<String> lore, List<String> enchantments, String... nbts) {
        return new ItemWrapper(
                material, amount, name, lore, enchantments, nbts
        );
    }

    /**
     * Load a new item
     * @param material for the ItemStack
     * @param amount of the ItemStack
     * @param name of the ItemStack
     * @param lore of the ItemStack
     * @return a ItemWrapper
     */
    public static ItemWrapper fromData(String material, int amount, String name, List<String> lore) {
        return fromData(
                material, amount, name, lore, Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(String material, String name, List<String> lore) {
        return fromData(
                material, 1, name, lore, Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(String material, int amount, String name) {
        return fromData(
                material, amount, name, Collections.emptyList(), Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(String material, String name) {
        return fromData(
                material, 1, name, Collections.emptyList(), Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(String material) {
        return fromData(
                material, 1, null, Collections.emptyList(), Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(String material, int amount) {
        return fromData(
                material, amount, material, Collections.emptyList(), Collections.emptyList()
        );
    }

    public static ItemWrapper fromData(ConfigurationSection configuration, String path) {
        return fromData(
                configuration.getConfigurationSection(path)
        );
    }

    public static ItemWrapper fromData(ConfigurationSection configuration) {
        if (configuration == null) {
            return fromData("POTION");
        }
        ItemWrapper wrapper = fromData(
                configuration.getString("material", "POTION"),
                configuration.getInt("amount", 1),
                configuration.getString("name", null),
                configuration.getStringList("lore"),
                configuration.getStringList("enchantments")
        );
        if (configuration.contains("charge-color")) {
            wrapper.setChargeMeta(
                    configuration.getString("charge-color", "red")
            );
        }
        return wrapper;
    }

    private void setChargeMeta(String value) {
        checkItem();

        if (item.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();

            FireworkEffect.Builder builder = FireworkEffect.builder();

            String[] split = value.replace(" ", "").split(":", 4);

            builder.withColor(
                    PluginTools.getColor(split[0])
            );

            if (split.length >= 2) {
                builder.flicker(
                        Boolean.parseBoolean(split[1])
                );
            }

            if (split.length >= 3) {
                builder.trail(
                        Boolean.parseBoolean(split[2])
                );
            }

            if (split.length == 4) {
                builder.withFade(
                        PluginTools.getColor(split[3])
                );
            }

            meta.setEffect(
                    builder.build()
            );

            item.setItemMeta(meta);
        }
    }



    private boolean hasPlaceholders() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public ItemWrapper copy() {
        checkItem();

        return new ItemWrapper(
                item.clone()
        );
    }
}

