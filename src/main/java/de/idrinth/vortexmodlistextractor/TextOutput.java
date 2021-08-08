package de.idrinth.vortexmodlistextractor;

public class TextOutput implements Output {
    private final StringBuilder output = new StringBuilder();
    @Override
    public void add(String modName, String file, String url) {
        output.append(modName);
        if (!file.isEmpty()) {
            output.append(" [");
            output.append(file);
            output.append("]");
        }
        if (!url.isEmpty()) {
            output.append(" => ");
            output.append(url);
        }
        output.append("\n");
    }

    @Override
    public String get() {
        return output.toString();
    }    

    @Override
    public String extension() {
        return "txt";
    }
}
