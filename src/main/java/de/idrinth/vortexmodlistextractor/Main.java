package de.idrinth.vortexmodlistextractor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    private static final String ATTR_PERSISTENT = "persistent";
    private static final String ATTR_MODS = "mods";
    private static final String ATTR_ATTRIBUTES = "attributes";
    private static final String ATTR_NAME = "modName";
    private static final String ATTR_ID = "modId";
    private static final String ATTR_HOMEPAGE = "homepage";
    private static final String ATTR_FILENAME = "logicalFileName";

    private static JSONObject getMods(File input) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(new FileReader(input.getAbsoluteFile()));
        if (object.containsKey(ATTR_PERSISTENT)) {
            JSONObject persistent = (JSONObject) object.get(ATTR_PERSISTENT);
            if (persistent.containsKey(ATTR_MODS)) {
                return (JSONObject) persistent.get(ATTR_MODS);
            }
        }
        throw new IllegalArgumentException("No mods defined");
    }

    private static void parseModlist(Output output, JSONObject mods, String game, boolean isCmdMode) {
        if (!mods.containsKey(game)) {
            return;
        }
        JSONObject gameMods = (JSONObject) mods.get(game);
        if (gameMods == null) {
            return;
        }
        for (Object key : gameMods.keySet()) {
            String modName = (String) key;
            String file = "";
            String url = "";
            JSONObject mod = (JSONObject) gameMods.get(key);
            if (mod != null && mod.containsKey(ATTR_ATTRIBUTES)) {
                JSONObject attributes = (JSONObject) mod.get(ATTR_ATTRIBUTES);
                if (attributes.containsKey(ATTR_NAME) && attributes.get(ATTR_NAME) != null) {
                    modName = (String) attributes.get(ATTR_NAME);
                }
                if (attributes.containsKey(ATTR_FILENAME) && attributes.get(ATTR_FILENAME) != null) {
                    file += (String) attributes.get(ATTR_FILENAME);
                }
                if (attributes.containsKey(ATTR_HOMEPAGE) && attributes.get(ATTR_HOMEPAGE) != null) {
                    url = (String) attributes.get(ATTR_HOMEPAGE);
                } else if (attributes.containsKey(ATTR_ID) && attributes.get(ATTR_ID) != null) {
                    url = "https://www.nexusmods.com/skyrimspecialedition/mods/" + String.valueOf(attributes.get(ATTR_ID));
                }
            }
            if (isCmdMode) {
                System.out.println(modName + " [" + file + "] => " + url);
            }
            output.add(modName, file, url);
        }
    }

    private static Config getSettings(String[] args) throws IOException, ParseException {
        File input;
        String game;
        String type;
        JSONObject mods;
        switch (args.length) {
            case 4:
                return new Config(new File(args[2]), getMods(new File(args[0])), args[1], args[3]);
            case 3:
                return new Config(new File(args[2]), getMods(new File(args[0])), args[1], "txt");
            case 2:
                input = new File(args[0]);
                return new Config(new File(input.getParent() + "/modlist." + args[1] + ".txt"), getMods(input), args[1], "txt");
            case 0:
                JOptionPane.showMessageDialog(null, "Please select the backup you just created.");
                JFileChooser j = new JFileChooser();
                int r = j.showOpenDialog(null);
                if (r != JFileChooser.APPROVE_OPTION) {
                    throw new IllegalArgumentException("You need to choose an input file.");
                }
                input = j.getSelectedFile();
                mods = getMods(input);
                game = (String) JOptionPane.showInputDialog(null, "Choose the game", "Game", JOptionPane.QUESTION_MESSAGE, null, mods.keySet().toArray(), null);
                if (game == null) {
                    throw new IllegalArgumentException("You need to choose a game.");
                }
                type = (String) JOptionPane.showInputDialog(null, "Choose the output format", "Output", JOptionPane.QUESTION_MESSAGE, null, new String[]{"txt", "html"}, null);
                if (type == null) {
                    throw new IllegalArgumentException("You need to choose an output format.");
                }
                return new Config(new File(input.getParent() + "/modlist." + game + "." + type), mods, game, type);
            default:
                throw new IllegalArgumentException("You have to call with either no parameters or with 2 (backup, game) or with 3 (backup, game, output) or with 4 (backup, game, output, extension)");
        }
    }

    public static void main(String[] args) {
        boolean isCmdMode = args.length > 0;
        try {
            Config config = getSettings(args);
            Output output = config.extension == "html" ? new HTMLOutput() : new TextOutput();
            parseModlist(output, config.mods, config.game, isCmdMode);
            try {
                if (!config.out.exists()) {
                    config.out.createNewFile();
                }
                try (FileWriter fw = new FileWriter(config.out)) {
                    fw.write(output.get());
                }
                if (!isCmdMode) {
                    JOptionPane.showMessageDialog(null, "Result was written to " + config.out.getAbsoluteFile() + ".");
                }
            } catch (IOException e) {
                if (!isCmdMode) {
                    JOptionPane.showMessageDialog(null, "Result couldn't be written to " + config.out.getAbsoluteFile() + ", please change the folder your file is in.");
                } else {
                    System.err.println(e);
                }
            }
        } catch (Exception ex) {
            if (!isCmdMode) {
                JOptionPane.showMessageDialog(null, "Failed to do my work: " + ex.getClass().getCanonicalName() + ": " + ex.getMessage());
            } else {
                System.err.println(ex);
            }
            System.exit(1);
        }
        System.exit(0);
    }
}
