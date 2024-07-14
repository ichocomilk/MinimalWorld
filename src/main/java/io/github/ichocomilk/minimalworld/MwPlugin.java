package io.github.ichocomilk.minimalworld;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.ichocomilk.minimalworld.commands.MwCommand;

public class MwPlugin extends JavaPlugin {

    public static final int WORLD_VERSION = 1;

    @Override
    public void onEnable() {
        final File worldFolder = new File(getDataFolder(), "worlds");
        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
        }
        saveDefaultConfig();
        getCommand("mw").setExecutor(new MwCommand(worldFolder, getConfig().getInt("compression-level")));
    }
}