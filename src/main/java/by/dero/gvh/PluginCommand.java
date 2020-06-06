package by.dero.gvh;

public interface PluginCommand {
    void execute(String[] argument);

    String getDescription();
}
