package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.lobby.utils.VoidGenerator;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Lobby implements PluginMode {
    private LobbyInfo info;
    private final String worldName = "Lobby";
    private World world;

    @Override
    public void onEnable() {
        registerEvents();
        try {
            info = new Gson().fromJson(DataUtils.loadOrDefault(new LocalStorage(), "lobby", "lobby",
                    ResourceUtils.readResourceFile("/lobby/lobby.json")), LobbyInfo.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // create world
        if (Plugin.getInstance().getServer().getWorld(worldName) == null) {
            WorldCreator creator = new WorldCreator(worldName);
            creator.generator(new VoidGenerator());
            world = creator.createWorld();
        }
        world = Plugin.getInstance().getServer().getWorld(worldName);
    }

    @Override
    public void onDisable() {}

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), Plugin.getInstance());
    }

    public LobbyInfo getInfo() {
        return info;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        return world;
    }
}
