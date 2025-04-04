package fr.wakleg;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * The fr.wakleg.Saver
 *
 * <p>
 * Save some things to a file.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
public class Saver {
    /**
     * The file where to save the things
     */
    private final Path file;

    /**
     * The properties object
     */
    private final Properties properties;

    /**
     * The fr.wakleg.Saver
     *
     * @param file The file where to save the things
     */
    public Saver(File file) {
        this(file.toPath());
    }

    public Saver(Path file) {
        this.file = file;
        this.properties = new Properties();

        if (Files.exists(this.file)) this.load();
        else {
            try {
                Files.createDirectories(this.file.getParent());
                Files.createFile(this.file);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Set something
     *
     * @param key   The key
     * @param value The value
     */
    public void set(String key, String value) {
        this.properties.setProperty(key, value);
        this.save();
    }

    /**
     * Loads something
     *
     * @param key The key of the thing to get
     * @return The value if founded, or null
     */
    public String get(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Loads somethings with a default value
     *
     * @param key The key of the thing to get
     * @param def The default value
     * @return The value if founded, or the def if not
     */
    public String get(String key, String def) {
        final String value = this.properties.getProperty(key);
        return value == null ? def : value;
    }

    /**
     * Save the properties (automatic when you do {@link #set(String, String)})
     */
    public void save() {
        try {
            final BufferedWriter writer = Files.newBufferedWriter(this.file);
            this.properties.store(writer, "Generated by the Iroxxy fr.wakleg.Saver");
            writer.close();
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    /**
     * Load the properties (automatic when you do {@link #Saver(Path)}
     */
    public void load() {
        try {
            this.properties.load(Files.newInputStream(this.file));
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    public void remove(String key) {
        this.properties.remove(key);
        this.save();
    }
}
