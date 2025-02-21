package me.blueslime.velocity.utilitiesapi.color;

import me.blueslime.velocity.utilitiesapi.color.types.VelocityColor;
import net.kyori.adventure.text.Component;

public abstract class ColorHandler {

    private static ColorHandler INSTANCE = null;

    public static ColorHandler get() {
        if (INSTANCE == null) {
            INSTANCE = create();
        }
        return INSTANCE;
    }

    private static ColorHandler create() {
        return new VelocityColor();
    }

    /**
     * Procesa el mensaje y retorna un {@link Component}.<br>
     * Se da prioridad al formato MiniMessage si se detectan tags (por ejemplo, &lt;rainbow&gt;).
     * En caso contrario, se procesa usando el serializer legacy que soporta códigos tradicionales
     * como &amp;a y &amp;#HEXCODE.
     *
     * @param message El mensaje a formatear.
     * @return El mensaje formateado como {@link Component}.
     */
    public abstract Component execute(String message);

    /**
     * Procesa el mensaje y retorna una cadena en formato legacy.<br>
     * Este método utiliza el serializer legacy para convertir el {@link Component} generado
     * por {@link #execute(String)} en una cadena con códigos de color.
     *
     * @param message El mensaje a formatear.
     * @return El mensaje formateado como {@code String} con códigos de color.
     */
    public abstract String executeAsString(String message);

    public static Component convert(String text) {
        return get().execute(text);
    }

    public static String convertAsString(String text) {
        return get().executeAsString(text);
    }

}