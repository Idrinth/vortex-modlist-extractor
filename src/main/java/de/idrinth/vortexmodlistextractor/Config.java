package de.idrinth.vortexmodlistextractor;

import java.io.File;
import org.json.simple.JSONObject;

public class Config {
    public final File out;
    public final JSONObject mods;
    public final String game;
    public final String extension;

    public Config(File out, JSONObject mods, String game, String extension) {
        this.out = out;
        this.mods = mods;
        this.game = game;
        this.extension = extension;
    }
}
