package de.idrinth.vortexmodlistextractor;

public interface Output {
    void add(String modName, String file, String url);
    String get();
    String extension();
}
