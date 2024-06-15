package me.blueslime.utilitiesapi.item.nbt;

import java.lang.reflect.Method;

import me.blueslime.nmshandlerapi.utils.presets.Presets;
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
        String name = Bukkit.getServer().getClass().getPackage().getName();

        try {
            this.originVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        } catch (Exception ignored) {
            this.originVersion = "1.8.8";
        }
        this.version = name.substring(
            name.lastIndexOf(".") + 1
        );

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

        try {
            this.setString = nbtCompound.getMethod("a", String.class, String.class);
            this.getString = nbtCompound.getMethod("l", String.class);
        } catch (Exception ignored) {
            try {
                this.setString = nbtCompound.getMethod("putString", String.class, String.class);
                this.getString = nbtCompound.getMethod("getString", String.class);
            } catch (Exception ignored2) { }
        }
    }

    public ItemStack setString(ItemStack stack, String k, String v) {
        if (!isVersionCompatible()) {
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
        if (!isVersionCompatible()) {
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isVersionCompatible() {
        return reflectionItem != null &&
            bukkitItem != null &&
            setString != null &&
            getString != null &&
            item != null &&
            hasTag != null &&
            getTag != null &&
            setTag != null;
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
}