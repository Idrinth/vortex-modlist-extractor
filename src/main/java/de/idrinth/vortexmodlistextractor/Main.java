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
    public static void main(String[] args) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        File input;
        String game;
        File fileOut;
        boolean isCmdMode = args.length > 0;
        if (args.length == 3) {
            input = new File(args[0]);
            game = args[1];
            fileOut = new File(args[2]);
        } else if (args.length == 2) {
            input = new File(args[0]);
            game = args[1];
            fileOut = new File(input.getParent() + "/modlist." + game + ".txt");
        } else if (args.length == 0) {
            JOptionPane.showMessageDialog(null, "Please select the backup you just created.");
            JFileChooser j = new JFileChooser();
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                input = j.getSelectedFile();
            } else {
                System.exit(1);
                return;
            }
            game = "skyrimse";
            fileOut = new File(input.getParent() + "/modlist." + game + ".txt");
        } else {
            System.err.println("You have to call with either no parameters or with 2 (backup, game) or with 3 (backup, game, output)");
            System.exit(1);
            return;
        }
        JSONObject object = (JSONObject) parser.parse(new FileReader(input.getAbsoluteFile()));
        StringBuilder output = new StringBuilder();
        if (object.containsKey("persistent")) {
            JSONObject persistent = (JSONObject) object.get("persistent");
            if (persistent.containsKey("mods")) {
                JSONObject mods = (JSONObject) persistent.get("mods");
                if (mods.containsKey(game)) {
                    JSONObject gameMods = (JSONObject) mods.get(game);
                    if (gameMods != null) {
                        for (Object key : gameMods.keySet()) {
                            String modName = (String) key;
                            String url = "";
                            JSONObject mod = (JSONObject) gameMods.get(key);
                            if (mod != null) {
                                JSONObject attributes = (JSONObject) mod.get("attributes");
                                if (attributes.containsKey("modName") && attributes.get("modName") != null) {
                                    modName = (String) attributes.get("modName");
                                }
                                if (attributes.containsKey("modId") && attributes.get("modId") != null) {
                                    url = "https://www.nexusmods.com/skyrimspecialedition/mods/" + (String.valueOf((Long) attributes.get("modId")));
                                } else if (attributes.containsKey("homepage") && attributes.get("homepage") != null) {
                                    url = (String) attributes.get("homepage");
                                }
                            }
                            if (isCmdMode) {
                                System.out.println(modName + " => " + url);
                            }
                            output.append(modName);
                            output.append(" => ");
                            output.append(url);
                            output.append("\n");
                        }
                    }
                }
            }
        }
        try {
            if (!fileOut.exists()) {
                fileOut.createNewFile();
            }
            try (FileWriter fw = new FileWriter(fileOut)) {
                fw.write(output.toString());
            }
            if (!isCmdMode) {
                JOptionPane.showMessageDialog(null, "Result was written to " + fileOut.getAbsoluteFile() + ".");
            }
        } catch(IOException e) {
            if (!isCmdMode) {
                JOptionPane.showMessageDialog(null, "Result couldn't be written to " + fileOut.getAbsoluteFile() + ", please change the folder your file is in.");
            } else {
                System.err.println(e);
            }
        }
        System.exit(0);
    }
}
