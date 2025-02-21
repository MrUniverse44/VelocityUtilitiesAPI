package me.blueslime.velocity.utilitiesapi.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import me.blueslime.velocity.utilitiesapi.tools.PluginTools;
import me.blueslime.velocity.utilitiesapi.utils.*;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

@SuppressWarnings("unused")
public class VelocityConfiguration {

    private static final LoaderOptions LOADER_OPTIONS = new LoaderOptions();
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();
    private static final Representer REPRESENTER;
    private static final Yaml YAML_INSTANCE;

    static {
        LOADER_OPTIONS.setProcessComments(true);
        DUMPER_OPTIONS.setIndent(2);
        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DUMPER_OPTIONS.setProcessComments(true);
        REPRESENTER = new Representer(DUMPER_OPTIONS);
        YAML_INSTANCE = new Yaml(new SafeConstructor(LOADER_OPTIONS), REPRESENTER, DUMPER_OPTIONS, LOADER_OPTIONS);
    }

    private final Map<String, String> comments;
    private final Map<String, Object> data;

    private final VelocityConfiguration parent;
    private final VelocityConfiguration root;

    private File file = null;

    public VelocityConfiguration() {
        this.comments = new LinkedHashMap<>();
        this.data = new LinkedHashMap<>();
        this.parent = this;
        this.root = this;
    }

    private VelocityConfiguration(VelocityConfiguration parent, VelocityConfiguration root, Map<String, Object> data) {
        this.comments = new LinkedHashMap<>();
        this.parent = parent != null ? parent : this;
        this.root = root != null ? root : this;
        this.data = (data != null)
            ? data
            : new LinkedHashMap<>();
    }

    public static Optional<VelocityConfiguration> loadConfig(File configFile, PluginExecutableConsumer<VelocityConfiguration> defaultConsumer) {
        return Optional.ofNullable(
            PluginConsumer.ofUnchecked(
                () -> load(configFile),
                e -> {},
                defaultConsumer
            )
        );
    }

    public static VelocityConfiguration load(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> loadedData = YAML_INSTANCE.load(reader);
            if (loadedData == null) {
                loadedData = new LinkedHashMap<>();
            }
            VelocityConfiguration config = new VelocityConfiguration(null, null, loadedData);
            config.file = file;
            return config;
        }
    }

    public void save(File file) throws IOException {
        String dumped = YAML_INSTANCE.dump(this.data);
        String finalYaml = injectComments(dumped);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(finalYaml);
        }
        this.file = file;
    }

    public void save() throws IOException {
        if (this.file == null) {
            throw new IllegalStateException("No file is associated with this configuration.");
        }
        save(this.file);
    }

    public void reload() throws IOException {
        if (this.file == null) {
            throw new IllegalStateException("No file is associated with this configuration.");
        }
        VelocityConfiguration reloaded = VelocityConfiguration.load(this.file);
        this.data.clear();
        this.data.putAll(reloaded.data);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setComment(String path, String comment) {
        comments.put(path, comment);
    }

    public Object get(String path) {
        return get(path, null);
    }

    public Object get(String path, Object defValue) {
        String[] parts = path.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) {
                return defValue;
            }
            if (!map.containsKey(part)) {
                return defValue;
            }
            current = map.get(part);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    public void set(String path, Object value, String... comments) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.containsKey(part) || !(current.get(part) instanceof Map)) {
                current.put(part, new LinkedHashMap<String, Object>());
            }
            current = (Map<String, Object>) current.get(part);
        }
        current.put(parts[parts.length - 1], value);

        if (comments != null && comments.length > 0) {
            String combined = String.join("\n", comments);
            setComment(path, combined);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object value = get(path);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return new ArrayList<>();
    }

    public List<String> getStringList(String path) {
        List<Object> list = getList(path);
        List<String> stringList = new ArrayList<>();
        for (Object o : list) {
            stringList.add(o.toString());
        }
        return stringList;
    }

    public List<Integer> getIntegerList(String path) {
        List<Object> list = getList(path);
        List<Integer> integerList = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Number) {
                integerList.add(((Number) o).intValue());
            } else {
                try {
                    integerList.add(Integer.parseInt(o.toString()));
                } catch (NumberFormatException ignored) { }
            }
        }
        return integerList;
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    public String getString(String path) {
        Object value = get(path);
        return (value != null) ? value.toString() : null;
    }

    public int getInt(String path) {
        return getInt(path, 0);
    }

    public int getInt(String path, int defValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        String stringValue = (value != null) ? value.toString() : null;

        if (stringValue == null) {
            return defValue;
        }

        return PluginTools.toInt(
            stringValue,
            defValue
        );
    }

    public double getDouble(String path) {
        return getDouble(path, 0);
    }

    public double getDouble(String path, double defValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        String stringValue = (value != null) ? value.toString() : null;

        if (stringValue == null) {
            return defValue;
        }

        return PluginTools.toDouble(
            stringValue,
            defValue
        );
    }

    public VelocityConfiguration getParent() {
        return parent;
    }

    public VelocityConfiguration getRoot() {
        return root;
    }

    public float getFloat(String path) {
        return getFloat(path, 0);
    }

    public float getFloat(String path, float defValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        String stringValue = (value != null) ? value.toString() : null;

        if (stringValue == null) {
            return defValue;
        }

        return PluginTools.toFloat(
            stringValue,
            defValue
        );
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean defValue) {
        Object value = get(path);

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        String stringValue = (value != null) ? value.toString() : null;

        if (stringValue == null) {
            return defValue;
        }
        return Boolean.parseBoolean(stringValue);
    }

    public VelocityConfiguration createSection(String path) {
        Map<String, Object> map = new LinkedHashMap<>();
        set(path, map);
        return new VelocityConfiguration(this, root, map);
    }

    @SuppressWarnings("unchecked")
    public Optional<VelocityConfiguration> getSection(String path) {
        Object section = get(path);
        if (section instanceof Map) {
            return Optional.of(new VelocityConfiguration(this, root, (Map<String, Object>) section));
        }
        return Optional.empty();
    }

    public Set<String> getKeys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>();
        collectKeys("", data, keys, deep);
        return keys;
    }

    @SuppressWarnings("unchecked")
    private void collectKeys(String prefix, Map<String, Object> map, Set<String> keys, boolean deep) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);
            if (deep && entry.getValue() instanceof Map) {
                collectKeys(key, (Map<String, Object>) entry.getValue(), keys, deep);
            }
        }
    }

    private String injectComments(String dumped) {
        StringBuilder finalYaml = new StringBuilder();
        String[] lines = dumped.split("\n");
        List<String> keyStack = new ArrayList<>();
        int indentSize = DUMPER_OPTIONS.getIndent();

        for (String line : lines) {
            int leadingSpaces = countLeadingSpaces(line);
            int currentDepth = leadingSpaces / indentSize;

            while (keyStack.size() > currentDepth) {
                keyStack.remove(keyStack.size() - 1);
            }
            String trimmed = line.trim();
            if (trimmed.matches("^[^\\s].*?:.*")) {
                int colonIndex = trimmed.indexOf(':');
                if (colonIndex > 0) {
                    String keyName = trimmed.substring(0, colonIndex).trim();
                    keyStack.add(keyName);
                    String fullPath = String.join(".", keyStack);
                    if (comments.containsKey(fullPath)) {
                        String comment = comments.get(fullPath);
                        finalYaml.append(" ".repeat(leadingSpaces))
                                .append("# ")
                                .append(comment)
                                .append("\n");
                    }
                }
            }
            finalYaml.append(line).append("\n");
        }
        return finalYaml.toString();
    }

    private int countLeadingSpaces(String s) {
        int count = 0;
        while (count < s.length() && s.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

}
