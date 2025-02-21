package me.blueslime.velocity.utilitiesapi.color.types;

import me.blueslime.velocity.utilitiesapi.color.ColorHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityColor extends ColorHandler {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
        LegacyComponentSerializer
            .builder()
            .hexColors()
            .character('&')
            .build();

    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    /**
     * Procesa el mensaje y retorna un {@link Component}.<br>
     * Se da prioridad al formato MiniMessage si se detectan tags (por ejemplo, &lt;rainbow&gt;).
     * En caso contrario, se procesa usando el serializer legacy que soporta códigos tradicionales
     * como &amp;a y &amp;#HEXCODE.
     *
     * @param message El mensaje a formatear.
     * @return El mensaje formateado como {@link Component}.
     */
    @Override
    public Component execute(String message) {
        if (message == null) {
            return Component.empty();
        }
        // Si se detectan tags de MiniMessage, se utiliza ese formato
        if (message.contains("<") && message.contains(">")) {
            return MINIMESSAGE.deserialize(message);
        } else {
            return LEGACY_SERIALIZER.deserialize(message);
        }
    }

    /**
     * Procesa el mensaje y retorna una cadena en formato legacy.<br>
     * Este método utiliza el serializer legacy para convertir el {@link Component} generado
     * por {@link #execute(String)} en una cadena con códigos de color.
     *
     * @param message El mensaje a formatear.
     * @return El mensaje formateado como {@code String} con códigos de color.
     */
    @Override
    public String executeAsString(String message) {
        Component component = execute(message);
        return LEGACY_SERIALIZER.serialize(component);
    }
}
