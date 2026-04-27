package com.azreyzaako.easytitle;

import com.azreyzaako.easytitle.commands.EasyTitleCmd;
import com.azreyzaako.easytitle.gui.TitleGui;
import com.azreyzaako.easytitle.managers.TitleSessionManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyTitle extends JavaPlugin implements Listener {

    private static EasyTitle instance;
    private TitleSessionManager sessionManager;
    private MiniMessage miniMessage;
    private MiniMessage safeMiniMessage;
    private BukkitAudiences adventure;
    private TitleGui titleGui;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.adventure = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.miniMessage();
        this.safeMiniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(StandardTags.gradient())
                        .resolver(StandardTags.rainbow())
                        .resolver(StandardTags.reset())
                        .resolver(StandardTags.newline())
                        .resolver(StandardTags.font())
                        .resolver(StandardTags.transition())
                        .build())
                .build();
        this.sessionManager = new TitleSessionManager(this);
        this.titleGui = new TitleGui(this);

        getServer().getPluginManager().registerEvents(this.titleGui, this);
        getServer().getPluginManager().registerEvents(this, this);

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

    public TitleGui getTitleGui() {
        return titleGui;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    /** Returns a restricted MiniMessage instance that excludes click/hover/insertion tags for security. */
    public MiniMessage getSafeMiniMessage() {
        return safeMiniMessage;
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sessionManager.remove(event.getPlayer().getUniqueId());
    }

    // -------------------------------------------------------------------------
    // Config helpers
    // -------------------------------------------------------------------------

    public int defaultFadeIn()  { return getConfig().getInt("defaults.fade-in",  10); }
    public int defaultStay()    { return getConfig().getInt("defaults.stay",      70); }
    public int defaultFadeOut() { return getConfig().getInt("defaults.fade-out",  20); }

    public String rawPrefix() {
        return legacyToMiniMessage(getConfig().getString("prefix",
                "<dark_gray>[<gold><bold>EasyTitle</bold></gold><dark_gray>]</dark_gray> "));
    }

    public String rawMessage(String key) {
        return legacyToMiniMessage(getConfig().getString("messages." + key, "<red>Missing message: " + key));
    }

    public static String legacyToMiniMessage(String text) {
        if (text == null) return null;
        // Handle both lowercase and uppercase legacy codes
        return text
                // Lowercase ampersand codes
                .replace("&0", "<black>").replace("&1", "<dark_blue>").replace("&2", "<dark_green>").replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>").replace("&5", "<dark_purple>").replace("&6", "<gold>").replace("&7", "<gray>")
                .replace("&8", "<dark_gray>").replace("&9", "<blue>").replace("&a", "<green>").replace("&b", "<aqua>")
                .replace("&c", "<red>").replace("&d", "<light_purple>").replace("&e", "<yellow>").replace("&f", "<white>")
                .replace("&k", "<obfuscated>").replace("&l", "<bold>").replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>").replace("&o", "<italic>").replace("&r", "<reset>")
                // Uppercase ampersand codes
                .replace("&A", "<green>").replace("&B", "<aqua>").replace("&C", "<red>").replace("&D", "<light_purple>")
                .replace("&E", "<yellow>").replace("&F", "<white>")
                .replace("&K", "<obfuscated>").replace("&L", "<bold>").replace("&M", "<strikethrough>")
                .replace("&N", "<underlined>").replace("&O", "<italic>").replace("&R", "<reset>")
                // Section sign codes (lowercase)
                .replace("§0", "<black>").replace("§1", "<dark_blue>").replace("§2", "<dark_green>").replace("§3", "<dark_aqua>")
                .replace("§4", "<dark_red>").replace("§5", "<dark_purple>").replace("§6", "<gold>").replace("§7", "<gray>")
                .replace("§8", "<dark_gray>").replace("§9", "<blue>").replace("§a", "<green>").replace("§b", "<aqua>")
                .replace("§c", "<red>").replace("§d", "<light_purple>").replace("§e", "<yellow>").replace("§f", "<white>")
                .replace("§k", "<obfuscated>").replace("§l", "<bold>").replace("§m", "<strikethrough>")
                .replace("§n", "<underlined>").replace("§o", "<italic>").replace("§r", "<reset>")
                // Section sign codes (uppercase)
                .replace("§A", "<green>").replace("§B", "<aqua>").replace("§C", "<red>").replace("§D", "<light_purple>")
                .replace("§E", "<yellow>").replace("§F", "<white>")
                .replace("§K", "<obfuscated>").replace("§L", "<bold>").replace("§M", "<strikethrough>")
                .replace("§N", "<underlined>").replace("§O", "<italic>").replace("§R", "<reset>");
    }
}
