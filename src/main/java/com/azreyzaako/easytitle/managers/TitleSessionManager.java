package com.azreyzaako.easytitle.managers;

import com.azreyzaako.easytitle.EasyTitle;
import com.cryptomorin.xseries.XSound;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Holds staged title/subtitle/timing data per command-sender (by UUID).
 * Console sessions use a fixed nil UUID.
 */
public class TitleSessionManager {

    /** Sentinel UUID used for the console "session". */
    public static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final EasyTitle plugin;

    /** Per-sender staged state. */
    private final Map<UUID, TitleSession> sessions = new HashMap<>();

    public TitleSessionManager(EasyTitle plugin) {
        this.plugin = plugin;
    }

    // -------------------------------------------------------------------------
    // Session access
    // -------------------------------------------------------------------------

    /** Returns the existing session for {@code uuid}, or creates a fresh one. */
    public TitleSession getOrCreate(UUID uuid) {
        return sessions.computeIfAbsent(uuid, k -> newDefaultSession());
    }

    /** Removes the session for {@code uuid}. */
    public void remove(UUID uuid) {
        sessions.remove(uuid);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private TitleSession newDefaultSession() {
        return new TitleSession(
                plugin.defaultFadeIn(),
                plugin.defaultStay(),
                plugin.defaultFadeOut()
        );
    }

    // =========================================================================
    // Inner record
    // =========================================================================

    /** Mutable staging area for a single sender's title. */
    public static class TitleSession {

        private String title    = "";
        private String subtitle = "";
        private String actionbar = "";
        private XSound sound = null;
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private int fadeIn;
        private int stay;
        private int fadeOut;

        public TitleSession(int fadeIn, int stay, int fadeOut) {
            this.fadeIn  = fadeIn;
            this.stay    = stay;
            this.fadeOut = fadeOut;
        }

        // Setters
        public void setTitle(String title)       { this.title    = title; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        public void setActionbar(String actionbar) { this.actionbar = actionbar; }
        public void setSound(XSound sound, float vol, float pit) { this.sound = sound; this.volume = vol; this.pitch = pit; }
        public void setFadeIn(int fadeIn)           { this.fadeIn   = fadeIn; }
        public void setStay(int stay)               { this.stay     = stay; }
        public void setFadeOut(int fadeOut)         { this.fadeOut  = fadeOut; }

        // Getters
        public String getTitle()    { return title; }
        public String getSubtitle() { return subtitle; }
        public String getActionbar() { return actionbar; }
        public XSound getSound()    { return sound; }
        public float getVolume()    { return volume; }
        public float getPitch()     { return pitch; }
        public int getFadeIn()         { return fadeIn; }
        public int getStay()           { return stay; }
        public int getFadeOut()        { return fadeOut; }

        public boolean hasContent() {
            return !title.isEmpty() || !subtitle.isEmpty() || !actionbar.isEmpty();
        }

        /** Builds a Title component by parsing Strings. */
        public Title buildTitle(Player target, EasyTitle plugin) {
            Title.Times times = Title.Times.times(
                    ticksToDuration(fadeIn),
                    ticksToDuration(stay),
                    ticksToDuration(fadeOut)
            );
            return Title.title(parse(title, target, plugin), parse(subtitle, target, plugin), times);
        }

        /** Sends this staged title and action bar to the given player. */
        public void sendTo(Player target, EasyTitle plugin) {
            if (!title.isEmpty() || !subtitle.isEmpty()) {
                plugin.adventure().player(target).showTitle(buildTitle(target, plugin));
            }
            if (!actionbar.isEmpty()) {
                plugin.adventure().player(target).sendActionBar(parse(actionbar, target, plugin));
            }
            if (sound != null) {
                sound.play(target, volume, pitch);
            }
        }

        private Component parse(String text, Player target, EasyTitle plugin) {
            if (text == null || text.isEmpty()) return Component.empty();
            String parsed = text;
            if (target != null && org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                parsed = PlaceholderAPI.setPlaceholders(target, parsed);
            }
            parsed = org.bukkit.ChatColor.translateAlternateColorCodes('&', parsed);
            return plugin.getMiniMessage().deserialize(parsed);
        }

        private static Duration ticksToDuration(int ticks) {
            return Duration.ofMillis(ticks * 50L);
        }
    }
}
