package me.blueslime.velocity.utilitiesapi.text;

import me.blueslime.velocity.utilitiesapi.configuration.VelocityConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextReplacer {
    private final Map<String, String> replacements;

    private TextReplacer() {
        this.replacements = new HashMap<>();
    }

    private TextReplacer(Map<String, String> replacements) {
        this.replacements = new HashMap<>(replacements);
    }

    /**
     * Replace a key with a value
     * @param key to be replaced
     * @param value of the replacement
     * @return TextReplacer
     */
    public TextReplacer replace(String key, String value) {
        replacements.put(key, value);
        return this;
    }

    /**
     * Add more replacements to this replacer
     * @param replacements to get replacements
     * @return this instance
     */
    public TextReplacer addReplacements(Map<String, String> replacements) {
        this.replacements.putAll(replacements);
        return this;
    }

    /**
     * Add more replacements from another replacement
     * @param replacer to copy replacements
     * @return this instance
     */
    public TextReplacer addReplacements(TextReplacer replacer) {
        this.replacements.putAll(replacer.replacements);
        return this;
    }

    /**
     * Replace a key with a value
     * @param key to be replaced
     * @param value of the replacement
     * @return TextReplacer
     */
    public TextReplacer replace(String key, int value) {
        replacements.put(key, String.valueOf(value));
        return this;
    }

    /**
     * Replace a key with a value
     * @param key to be replaced
     * @param value of the replacement
     * @return TextReplacer
     */
    public TextReplacer replace(String key, double value) {
        replacements.put(key, String.valueOf(value));
        return this;
    }

    /**
     * Replace a key with a value
     * @param key to be replaced
     * @param value of the replacement
     * @return TextReplacer
     */
    public TextReplacer replace(String key, float value) {
        replacements.put(key, String.valueOf(value));
        return this;
    }

    /**
     * Replace a key value from a configuration path
     * @param key to be replaced
     * @param configuration to search the path
     * @param path of the replacement
     * @param def value if the path is not set
     * @return TextReplacer
     */
    public TextReplacer replace(String key, VelocityConfiguration configuration, String path, Object def) {
        Object ob = configuration.get(path, def);

        if (ob == null) {
            replacements.put(key, "");
            return this;
        }

        if (ob instanceof List) {
            List<String> joining = new ArrayList<>();
            List<?> list = (List<?>)ob;

            list.forEach(object -> joining.add(object.toString()));

            replacements.put(key, String.join(", ", joining));
        } else {
            replacements.put(
                    key,
                    ob.toString()
            );
        }
        return this;
    }

    /**
     * Replace a key value from a configuration path
     * @param key to be replaced
     * @param configuration to search the path
     * @param path of the replacement
     * @return TextReplacer
     */
    public TextReplacer replace(String key, VelocityConfiguration configuration, String path) {
        return replace(key, configuration, path, null);
    }

    /**
     * Apply all replacements
     * @param text to be replaced
     * @return replaced text
     */
    public String apply(String text) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(
                entry.getKey(),
                entry.getValue()
            );
        }
        return text;
    }

    /**
     * Apply all replacements for a String List
     */
    public List<String> applyAll(List<String> textList) {
        textList.replaceAll(this::apply);
        return textList;
    }

    /**
     * Create a new builder instance of TextReplacer
     * @return TextReplacer
     */
    public static TextReplacer builder() {
        return new TextReplacer();
    }

    /**
     * Create a copy of current replacements for replacements per player or
     * different values
     * @return TextReplacer
     */
    public TextReplacer copy() {
        return new TextReplacer(replacements);
    }
}
