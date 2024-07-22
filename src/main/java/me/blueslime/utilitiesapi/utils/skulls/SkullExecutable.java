package me.blueslime.utilitiesapi.utils.skulls;

import org.bukkit.inventory.ItemStack;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public interface SkullExecutable {


    boolean attemptNewBase64(ItemStack head, String base64);

    default URL getUrlFromBase64(String base64) throws MalformedURLException {
        if (base64.contains("textures.minecraft.net")) {
            return new URL(base64);
        }
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }
}


