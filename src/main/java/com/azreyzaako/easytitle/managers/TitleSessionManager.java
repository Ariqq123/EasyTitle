package com.azreyzaako.easytitle.managers;

import com.azreyzaako.easytitle.EasyTitle;
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

        private Component title    = Component.empty();
        private Component subtitle = Component.empty();
        private int fadeIn;
        private int stay;
        private int fadeOut;

        public TitleSession(int fadeIn, int stay, int fadeOut) {
            this.fadeIn  = fadeIn;
            this.stay    = stay;
            this.fadeOut = fadeOut;
        }

        // Setters
        public void setTitle(Component title)       { this.title    = title; }
        public void setSubtitle(Component subtitle) { this.subtitle = subtitle; }
        public void setFadeIn(int fadeIn)           { this.fadeIn   = fadeIn; }
        public void setStay(int stay)               { this.stay     = stay; }
        public void setFadeOut(int fadeOut)         { this.fadeOut  = fadeOut; }

        // Getters
        public Component getTitle()    { return title; }
        public Component getSubtitle() { return subtitle; }
        public int getFadeIn()         { return fadeIn; }
        public int getStay()           { return stay; }
        public int getFadeOut()        { return fadeOut; }

        public boolean hasContent() {
            return !title.equals(Component.empty()) || !subtitle.equals(Component.empty());
        }

        /**
         * Builds an Adventure {@link Title} from the current staged values.
         * Timing is converted from ticks to milliseconds for {@link Duration}.
         */
        public Title buildTitle() {
            Title.Times times = Title.Times.times(
                    ticksToDuration(fadeIn),
                    ticksToDuration(stay),
                    ticksToDuration(fadeOut)
            );
            return Title.title(title, subtitle, times);
        }

        /** Sends this staged title to the given player. */
        public void sendTo(Player target, EasyTitle plugin) {
            plugin.adventure().player(target).showTitle(buildTitle());
        }

        private static Duration ticksToDuration(int ticks) {
            return Duration.ofMillis(ticks * 50L);
        }
    }
}
