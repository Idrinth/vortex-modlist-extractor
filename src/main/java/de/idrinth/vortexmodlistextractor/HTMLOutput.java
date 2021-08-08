package de.idrinth.vortexmodlistextractor;

public class HTMLOutput implements Output {
    private final StringBuilder output = new StringBuilder();
    
    @Override
    public void add(String modName, String file, String url) {
        output.append("<li>");
        if (!url.isEmpty()) {
            output.append("<a href=\"");
            output.append(url);
            output.append("\" target=\"_blank\">");
        }
        output.append(modName);
        if (!file.isEmpty()) {
            output.append(" [");
            output.append(file);
            output.append("]");
        }
        if (!url.isEmpty()) {
            output.append("</a>");
        }
        output.append("</li>");
        output.append("\n");
    }

    @Override
    public String get() {
        return "<!DOCTYPE html><html><body><ul>" + output.toString() + "</ul></body></html>";
    }    

    @Override
    public String extension() {
        return "html";
    }
}
