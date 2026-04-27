package com.azreyzaako.easytitle;

import com.azreyzaako.easytitle.commands.EasyTitleCmd;
import com.azreyzaako.easytitle.managers.TitleSessionManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyTitle extends JavaPlugin {

    private static EasyTitle instance;
    private TitleSessionManager sessionManager;
    private MiniMessage miniMessage;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.adventure = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.miniMessage();
        this.sessionManager = new TitleSessionManager(this);

        EasyTitleCmd cmdExecutor = new EasyTitleCmd(this);
        PluginCommand cmd = getCommand("etitle");
        if (cmd != null) {
            cmd.setExecutor(cmdExecutor);
            cmd.setTabCompleter(cmdExecutor);
        }

        getLogger().info("EasyTitle v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        getLogger().info("EasyTitle disabled.");
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public static EasyTitle getInstance() {
        return instance;
    }

    public TitleSessionManager getSessionManager() {
        return sessionManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    // -------------------------------------------------------------------------
    // Config helpers
    // -------------------------------------------------------------------------

    public int defaultFadeIn()  { return getConfig().getInt("defaults.fade-in",  10); }
    public int defaultStay()    { return getConfig().getInt("defaults.stay",      70); }
    public int defaultFadeOut() { return getConfig().getInt("defaults.fade-out",  20); }

    public String rawPrefix() {
        return getConfig().getString("prefix",
                "<dark_gray>[<gold><bold>EasyTitle</bold></gold><dark_gray>]</dark_gray> ");
    }

    public String rawMessage(String key) {
        return getConfig().getString("messages." + key, "<red>Missing message: " + key);
    }
}
