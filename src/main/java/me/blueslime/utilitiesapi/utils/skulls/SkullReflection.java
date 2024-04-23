package me.blueslime.utilitiesapi.utils.skulls;

import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullReflection {
    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");

    private static final Method SKULL_META_OWNER_PROFILE = getOwnerProfile();
    private static final Class<?> PROFILE_CLASS = getProfileClass();
    private static final Class<?> TEXTURE_CLASS = getTextureClass();
    private static final Method BUKKIT_CREATE_PROFILE = getCreateProfileMethod();
    private static final Method TEXTURE_GET_TEXTURES = getTextureMethod();
    private static final Method TEXTURE_SET_SKIN = setSkinMethod();
    private static final Method PROFILE_SET_TEXTURE = setTextures();

    private static boolean checkInitialization() {
        return SKULL_META_OWNER_PROFILE != null &&
                PROFILE_CLASS != null &&
                TEXTURE_CLASS != null &&
                BUKKIT_CREATE_PROFILE != null &&
                TEXTURE_GET_TEXTURES != null &&
                TEXTURE_SET_SKIN != null &&
                PROFILE_SET_TEXTURE != null;
    }

    private static Method getOwnerProfile() {
        return PluginConsumer.ofUnchecked(
                () -> SkullMeta.class.getDeclaredMethod("setOwnerProfile", getProfileClass()),
                e -> {},
                null
        );
    }

    private static Class<?> getProfileClass() {
        return PluginConsumer.ofUnchecked(
                () -> Class.forName("org.bukkit.profile.PlayerProfile"),
                e -> {},
                null
        );
    }

    private static Class<?> getTextureClass() {
        return PluginConsumer.ofUnchecked(
                () -> Class.forName("org.bukkit.profile.PlayerTextures"),
                e -> {},
                null
        );
    }

    private static Method getCreateProfileMethod() {
        return PluginConsumer.ofUnchecked(
                () -> Bukkit.class.getDeclaredMethod("createPlayerProfile", UUID.class),
                e -> {},
                null
        );
    }

    private static Method getTextureMethod() {
        return PluginConsumer.ofUnchecked(
                () -> {
                    Class<?> textureClass = getProfileClass();
                    if (textureClass == null) {
                        return null;
                    }
                    return textureClass.getDeclaredMethod("getTextures");
                },
                e -> {},
                null
        );
    }

    private static Method setSkinMethod() {
        return PluginConsumer.ofUnchecked(
                () -> {
                    Class<?> textureClass = getTextureClass();
                    if (textureClass == null) {
                        return null;
                    }
                    return textureClass.getDeclaredMethod("setSkin", URL.class);
                },
                e -> {},
                null
        );
    }

    private static Method setTextures() {
        return PluginConsumer.ofUnchecked(
                () -> {
                    Class<?> profileClass = getProfileClass();
                    Class<?> textureClass = getTextureClass();
                    if (profileClass == null || textureClass == null) {
                        return null;
                    }
                    return profileClass.getDeclaredMethod("setTextures", textureClass);
                },
                e -> {},
                null
        );
    }

    private static Object getProfileBase64(String base64) {
        return PluginConsumer.ofUnchecked(
                () -> {
                    Object profile = BUKKIT_CREATE_PROFILE.invoke(null, RANDOM_UUID);
                    Object textures = TEXTURE_GET_TEXTURES.invoke(profile);
                    URL urlObject;
                    try {
                        urlObject = getUrlFromBase64(base64);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return null;
                    }
                    TEXTURE_SET_SKIN.invoke(textures, urlObject);
                    PROFILE_SET_TEXTURE.invoke(profile, textures);
                    return profile;
                },
                e -> {},
                null
        );
    }

    public static boolean attemptNewBase64(ItemStack head, String base64) {
        if (checkInitialization()) {
            return PluginConsumer.ofUnchecked(
                    () -> {
                        if (head != null && head.getItemMeta() != null) {
                            SkullMeta meta = (SkullMeta) head.getItemMeta();

                            Object profile = getProfileBase64(base64);

                            if (profile != null) {
                                SKULL_META_OWNER_PROFILE.invoke(meta, profile);
                                head.setItemMeta(meta);
                            }
                        }
                        return true;
                    },
                    e -> {},
                    false
            );
        }
        return false;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        if (base64.contains("textures.minecraft.net")) {
            return new URL(base64);
        }
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }
}

