package me.blueslime.utilitiesapi.utils.skulls;

import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkullReflection implements SkullExecutable{
    private final Map<String, Object> playerProfileStorage = new ConcurrentHashMap<>();

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");

    private static final Method SKULL_META_OWNER_PROFILE = getOwnerProfile();
    private static final Class<?> PROFILE_CLASS = getProfileClass();
    private static final Class<?> TEXTURE_CLASS = getTextureClass();
    private static final Method BUKKIT_CREATE_PROFILE = getCreateProfileMethod();
    private static final Method TEXTURE_GET_TEXTURES = getTextureMethod();
    private static final Method TEXTURE_SET_SKIN = setSkinMethod();
    private static final Method PROFILE_SET_TEXTURE = setTextures();

    private static boolean checkInitialization() {
        check(SKULL_META_OWNER_PROFILE, BUKKIT_CREATE_PROFILE, TEXTURE_GET_TEXTURES, TEXTURE_SET_SKIN, PROFILE_SET_TEXTURE);
        return SKULL_META_OWNER_PROFILE != null &&
            PROFILE_CLASS != null &&
            TEXTURE_CLASS != null &&
            BUKKIT_CREATE_PROFILE != null &&
            TEXTURE_GET_TEXTURES != null &&
            TEXTURE_SET_SKIN != null &&
            PROFILE_SET_TEXTURE != null;
    }

    private static void check(Method... methods) {
        PluginConsumer.process(
            () -> {
                for (Method method : methods) {
                    if (method != null) {
                        method.setAccessible(true);
                    }
                }
            },
            e -> {}
        );
    }

    private static Method getOwnerProfile() {
        return PluginConsumer.ofUnchecked(
                () -> SkullMeta.class.getDeclaredMethod("setOwnerProfile", getProfileClass()),
                e -> {},
                () -> null
        );
    }

    private static Class<?> getProfileClass() {
        return PluginConsumer.ofUnchecked(
                () -> Class.forName("org.bukkit.profile.PlayerProfile"),
                e -> {},
                () -> null
        );
    }

    private static Class<?> getTextureClass() {
        return PluginConsumer.ofUnchecked(
                () -> Class.forName("org.bukkit.profile.PlayerTextures"),
                e -> {},
                () -> null
        );
    }

    private static Method getCreateProfileMethod() {
        return PluginConsumer.ofUnchecked(
                () -> Bukkit.class.getDeclaredMethod("createPlayerProfile", UUID.class, String.class),
                e -> {},
                () -> null
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
                () -> null
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
                () -> null
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
                () -> null
        );
    }

    private Object getProfileBase64(String base64) {
        return PluginConsumer.ofUnchecked(
                () -> {
                    if (playerProfileStorage.containsKey(base64)) {
                        return playerProfileStorage.get(base64);
                    }
                    Object profile = BUKKIT_CREATE_PROFILE.invoke(null, RANDOM_UUID, "RandomBoy");
                    Object textures = TEXTURE_GET_TEXTURES.invoke(profile);

                    URL urlObject = PluginConsumer.ofUnchecked(() -> getUrlFromBase64(base64), e -> {}, () -> null);

                    if (urlObject == null) {
                        return null;
                    }

                    TEXTURE_SET_SKIN.invoke(textures, urlObject);
                    PROFILE_SET_TEXTURE.invoke(profile, textures);
                    playerProfileStorage.put(base64, profile);
                    return profile;
                },
                e -> {},
                () -> null
        );
    }

    public boolean attemptNewBase64(ItemStack head, String base64) {
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
                    () -> false
            );
        }
        return false;
    }
}

