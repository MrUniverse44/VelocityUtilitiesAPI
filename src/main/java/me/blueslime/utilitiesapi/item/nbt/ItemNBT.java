package me.blueslime.utilitiesapi.item.nbt;

import java.lang.reflect.Method;

import me.blueslime.nmshandlerapi.utils.presets.Presets;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemNBT {
    public Method reflectionItem = null;
    public Method bukkitItem = null;

    public Method setString = null;
    public Method getString = null;

    public Class<?> item = null;
    public Method hasTag = null;
    public Method getTag = null;
    public Method setTag = null;

    protected String originVersion;
    protected final String version;

    private static ItemNBT ITEM_NBT;

    private static ItemNBT getInstance() {
        if (ITEM_NBT == null) {
            ITEM_NBT = new ItemNBT();
        }
        return ITEM_NBT;
    }

    private ItemNBT() {

        // Returns 1.8.8 for example
        this.originVersion = PluginConsumer.ofUnchecked(
                () -> Bukkit.getServer().getBukkitVersion().split("-")[0],
                e -> {},
                () -> "1.21"
        );

        // Returns org.bukkit.craftbukkit.v(version)
        String name = PluginConsumer.ofUnchecked(
            () -> Bukkit.getServer().getClass().getPackage().getName(),
            e -> {},
            () -> "org.bukkit.craftbukkit.v1_21_R1"
        );

        this.version = name.substring(
            name.lastIndexOf(".") + 1
        );

        // From 1.21 this version to new versions some things
        // Are not supported for reflection, so to prevent issues in console, we prepared this.
        if (isVersionIncompatible()) {
            // Don't need to load this because now uses Persistent Data in the newest versions.
            return;
        }

        try {
            Class<?> nbtCompound = Presets.NBT_COMPOUND.getResult();
            Class<?> itemStack = Presets.CRAFT_ITEM.getResult();

            this.reflectionItem = itemStack.getMethod("asNMSCopy", ItemStack.class);

            this.item = reflectionItem.getReturnType();

            this.bukkitItem = itemStack.getMethod("asBukkitCopy", item);

            if (isSpecified("v1_18_R1")) {
                this.hasTag = item.getMethod("r");
                this.getTag = item.getMethod("s");
                this.setTag = item.getMethod("c", nbtCompound);

                secondAttempt();
            } else if (isSpecified("v1_18_R2")) {
                this.hasTag = item.getMethod("s");
                this.getTag = item.getMethod("t");
                this.setTag = item.getMethod("c", nbtCompound);

                secondAttempt();
            } else if (isSpecified("v1_19_R1") || isSpecified("v1_19_R2") || isSpecified("v1_19_R3")) {
                this.hasTag = item.getMethod("t");
                this.getTag = item.getMethod("u");
                this.setTag = item.getMethod("c", nbtCompound);

                secondAttempt();
            } else if (isSpecified("v1_20_R0") || isSpecified("v1_20_R1") || isSpecified("v1_20_R2") || isSpecified("v1_20_R3") || isSpecified("v1_20_R4") || isSpecified("v1_20_R5") || isSpecified("v1_20_R6")) {
                this.hasTag = item.getMethod("u");
                this.getTag = item.getMethod("v");
                this.setTag = item.getMethod("c", nbtCompound);

                secondAttempt();
            } else {
                this.hasTag = item.getMethod("hasTag");
                this.getTag = item.getMethod("getTag");
                this.setTag = item.getMethod("setTag", nbtCompound);

                this.setString = nbtCompound.getMethod("setString", String.class, String.class);
                this.getString = nbtCompound.getMethod("getString", String.class);
            }
        } catch (ReflectiveOperationException ignored) {

        }
    }

    public void secondAttempt() {
        Class<?> nbtCompound = Presets.NBT_COMPOUND.getResult();

        if (nbtCompound == null) {
            return;
        }

        this.setString = PluginConsumer.ofUnchecked(
            () -> nbtCompound.getMethod("a", String.class, String.class),
            e -> {},
            () -> PluginConsumer.ofUnchecked(
                () -> nbtCompound.getMethod("putString", String.class, String.class),
                e -> {},
                () -> null
            )
        );

        this.setString = PluginConsumer.ofUnchecked(
            () -> nbtCompound.getMethod("l", String.class),
            e -> {},
            () -> PluginConsumer.ofUnchecked(
                () -> nbtCompound.getMethod("getString", String.class),
                e -> {},
                () -> null
            )
        );
    }

    public ItemStack setString(ItemStack stack, String k, String v) {
        if (isVersionIncompatible()) {
            return PersistentDataNBT.setString(stack, k, v);
        }
        try {
            Object item = reflectionItem.invoke(null, stack);

            Boolean hasTag = (Boolean) this.hasTag.invoke(item);

            final Object tag;

            if (hasTag) {
                tag = getTag.invoke(item);
            } else {
                tag = Presets.NBT_COMPOUND.getResult()
                        .getConstructor()
                        .newInstance();
            }

            setString.invoke(tag, k, v);

            setTag.invoke(item, tag);

            return (ItemStack) bukkitItem.invoke(null, item);
        } catch (ReflectiveOperationException ignored) {}

        return stack;
    }

    protected String getString(ItemStack stack, String k) {
        if (isVersionIncompatible()) {
            return PersistentDataNBT.getString(stack, k);
        }
        try {
            Object item = reflectionItem.invoke(null, stack);

            boolean hasTag = (boolean) this.hasTag.invoke(item);

            if (hasTag) {
                Object tag = getTag.invoke(item);

                return (String) getString.invoke(tag, k);
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    public static ItemStack addString(ItemStack stack, String k, String v) {
        return getInstance().setString(stack, k, v);
    }

    private boolean isVersionIncompatible() {
        return isUpperOrEqualThan(1.20);
    }

    @SuppressWarnings("unused")
    public static String fromString(ItemStack stack, String k) {
        String val = getInstance().getString(stack, k);
        if (val == null) {
            return "";
        }
        return val;
    }

    private boolean isSpecified(String version) {
        return this.version.equals(version);
    }

    private boolean isUpperOrEqualThan(double version) {
        return convertClassVersionToNumber(this.version) >= version ||
                convertVersionToNumber(originVersion) >= version;
    }

    private double convertVersionToNumber(String version) {
        try {
            version = version.replace("v", "").split("_")[0];
            String[] parts = version.split("\\.");

            int major = Integer.parseInt(parts[0]);
            int minor = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

            return major + (minor / 10.0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private double convertClassVersionToNumber(String version) {
        try {
            String[] parts = version.replace("v", "").split("_");
            return Double.parseDouble(parts[0] + "." + parts[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}