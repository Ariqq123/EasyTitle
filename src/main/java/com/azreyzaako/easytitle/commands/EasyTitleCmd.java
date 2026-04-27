package com.azreyzaako.easytitle.commands;

import com.azreyzaako.easytitle.EasyTitle;
import com.azreyzaako.easytitle.managers.TitleSessionManager;
import com.azreyzaako.easytitle.managers.TitleSessionManager.TitleSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the /etitle command and all its subcommands.
 *
 * <pre>
 *   /etitle help
 *   /etitle title &lt;MiniMessage text&gt;
 *   /etitle subtitle &lt;MiniMessage text&gt;
 *   /etitle times &lt;fadeIn&gt; &lt;stay&gt; &lt;fadeOut&gt;
 *   /etitle preview
 *   /etitle send &lt;player|*&gt;
 *   /etitle broadcast
 *   /etitle actionbar &lt;player|*&gt; &lt;MiniMessage text&gt;
 *   /etitle clear [player]
 *   /etitle reload
 * </pre>
 */
public class EasyTitleCmd implements CommandExecutor, TabCompleter {

    private final EasyTitle plugin;
    private final MiniMessage mm;
    private final TitleSessionManager sessionManager;

    public EasyTitleCmd(EasyTitle plugin) {
        this.plugin         = plugin;
        this.mm             = plugin.getMiniMessage();
        this.sessionManager = plugin.getSessionManager();
    }

    // =========================================================================
    // CommandExecutor
    // =========================================================================

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("easytitle.use")) {
            reply(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help":
                sendHelp(sender);
                break;
            case "title":
                handleTitle(sender, args);
                break;
            case "subtitle":
                handleSubtitle(sender, args);
                break;
            case "times":
                handleTimes(sender, args);
                break;
            case "preview":
                handlePreview(sender);
                break;
            case "send":
                handleSend(sender, args);
                break;
            case "broadcast":
                handleBroadcast(sender);
                break;
            case "actionbar":
                handleActionBar(sender, args);
                break;
            case "clear":
                handleClear(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    // =========================================================================
    // Subcommand handlers
    // =========================================================================

    /** /etitle title <text...> */
    private void handleTitle(CommandSender sender, String[] args) {
        if (args.length < 2) { reply(sender, "usage-title"); return; }

        String raw  = joinFrom(args, 1);
        raw = org.bukkit.ChatColor.translateAlternateColorCodes('&', raw);
        Component c = mm.deserialize(raw);
        getSession(sender).setTitle(c);
        replyRaw(sender, plugin.rawMessage("title-staged")
                .replace("<text>", raw));
    }

    /** /etitle subtitle <text...> */
    private void handleSubtitle(CommandSender sender, String[] args) {
        if (args.length < 2) { reply(sender, "usage-subtitle"); return; }

        String raw  = joinFrom(args, 1);
        raw = org.bukkit.ChatColor.translateAlternateColorCodes('&', raw);
        Component c = mm.deserialize(raw);
        getSession(sender).setSubtitle(c);
        replyRaw(sender, plugin.rawMessage("subtitle-staged")
                .replace("<text>", raw));
    }

    /** /etitle times <fadeIn> <stay> <fadeOut> */
    private void handleTimes(CommandSender sender, String[] args) {
        if (args.length < 4) { reply(sender, "usage-times"); return; }

        try {
            int fi = Integer.parseInt(args[1]);
            int st = Integer.parseInt(args[2]);
            int fo = Integer.parseInt(args[3]);

            if (fi < 0 || st < 0 || fo < 0) {
                replyRaw(sender, "<red>Values must be non-negative.");
                return;
            }

            TitleSession session = getSession(sender);
            session.setFadeIn(fi);
            session.setStay(st);
            session.setFadeOut(fo);

            replyRaw(sender, plugin.rawMessage("times-set")
                    .replace("<fi>", String.valueOf(fi))
                    .replace("<st>", String.valueOf(st))
                    .replace("<fo>", String.valueOf(fo)));
        } catch (NumberFormatException e) {
            replyRaw(sender, "<red>All values must be integers (ticks).");
        }
    }

    /** /etitle preview — shows staged title to the sender themselves. */
    private void handlePreview(CommandSender sender) {
        if (!(sender instanceof Player)) { reply(sender, "player-only"); return; }
        Player player = (Player) sender;

        TitleSession session = getSession(sender);
        if (!session.hasContent()) { reply(sender, "nothing-staged"); return; }

        session.sendTo(player, plugin);
    }

    /** /etitle send <player|*> */
    private void handleSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("easytitle.send")) { reply(sender, "no-permission"); return; }
        if (args.length < 2) { reply(sender, "usage-send"); return; }

        TitleSession session = getSession(sender);
        if (!session.hasContent()) { reply(sender, "nothing-staged"); return; }

        String targetArg = args[1];

        if (targetArg.equals("*")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                session.sendTo(p, plugin);
            }
            replyRaw(sender, plugin.rawMessage("broadcast-sent"));
        } else {
            Player target = Bukkit.getPlayerExact(targetArg);
            if (target == null) {
                replyRaw(sender, plugin.rawMessage("player-not-found")
                        .replace("<target>", targetArg));
                return;
            }
            session.sendTo(target, plugin);
            replyRaw(sender, plugin.rawMessage("sent")
                    .replace("<target>", target.getName()));
        }
    }

    /** /etitle broadcast — convenience alias for /etitle send * */
    private void handleBroadcast(CommandSender sender) {
        if (!sender.hasPermission("easytitle.broadcast")) { reply(sender, "no-permission"); return; }

        TitleSession session = getSession(sender);
        if (!session.hasContent()) { reply(sender, "nothing-staged"); return; }

        for (Player p : Bukkit.getOnlinePlayers()) {
            session.sendTo(p, plugin);
        }
        replyRaw(sender, plugin.rawMessage("broadcast-sent"));
    }

    /** /etitle actionbar <player|*> <text...> */
    private void handleActionBar(CommandSender sender, String[] args) {
        if (!sender.hasPermission("easytitle.send")) { reply(sender, "no-permission"); return; }
        if (args.length < 3) { reply(sender, "usage-actionbar"); return; }

        String targetArg = args[1];
        String raw       = joinFrom(args, 2);
        raw = org.bukkit.ChatColor.translateAlternateColorCodes('&', raw);
        Component bar    = mm.deserialize(raw);

        if (targetArg.equals("*")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.adventure().player(p).sendActionBar(bar);
            }
            replyRaw(sender, plugin.rawMessage("actionbar-sent")
                    .replace("<target>", "everyone"));
        } else {
            Player target = Bukkit.getPlayerExact(targetArg);
            if (target == null) {
                replyRaw(sender, plugin.rawMessage("player-not-found")
                        .replace("<target>", targetArg));
                return;
            }
            plugin.adventure().player(target).sendActionBar(bar);
            replyRaw(sender, plugin.rawMessage("actionbar-sent")
                    .replace("<target>", target.getName()));
        }
    }

    /** /etitle clear [player] */
    private void handleClear(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            // Clear another player
            if (!sender.hasPermission("easytitle.send")) { reply(sender, "no-permission"); return; }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                replyRaw(sender, plugin.rawMessage("player-not-found")
                        .replace("<target>", args[1]));
                return;
            }
            plugin.adventure().player(target).clearTitle();
            replyRaw(sender, plugin.rawMessage("cleared")
                    .replace("<target>", target.getName()));
        } else {
            // Clear self
            if (!(sender instanceof Player)) { reply(sender, "player-only"); return; }
            Player player = (Player) sender;
            plugin.adventure().player(player).clearTitle();
            replyRaw(sender, plugin.rawMessage("cleared")
                    .replace("<target>", player.getName()));
        }
    }

    /** /etitle reload */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("easytitle.reload")) { reply(sender, "no-permission"); return; }
        plugin.reloadConfig();
        reply(sender, "reloaded");
    }

    // =========================================================================
    // Help
    // =========================================================================

    private void sendHelp(CommandSender sender) {
        Component header = mm.deserialize(
                "<dark_gray><st>                </st> <gold><bold>EasyTitle</bold></gold> <dark_gray><st>                </st>");
        Component footer = mm.deserialize(
                "<dark_gray><st>                                                </st>");

        plugin.adventure().sender(sender).sendMessage(header);
        sendHelpLine(sender, "/etitle title <text>",             "Stage a title (MiniMessage)");
        sendHelpLine(sender, "/etitle subtitle <text>",          "Stage a subtitle");
        sendHelpLine(sender, "/etitle times <fi> <st> <fo>",     "Set fade-in / stay / fade-out (ticks)");
        sendHelpLine(sender, "/etitle preview",                   "Preview staged title on yourself");
        sendHelpLine(sender, "/etitle send <player|*>",           "Send staged title to a player or all");
        sendHelpLine(sender, "/etitle broadcast",                 "Broadcast staged title to everyone");
        sendHelpLine(sender, "/etitle actionbar <player|*> <t>", "Send an action bar message");
        sendHelpLine(sender, "/etitle clear [player]",            "Clear title for yourself or a player");
        sendHelpLine(sender, "/etitle reload",                    "Reload config.yml");
        plugin.adventure().sender(sender).sendMessage(footer);
    }

    private void sendHelpLine(CommandSender sender, String usage, String desc) {
        plugin.adventure().sender(sender).sendMessage(mm.deserialize(
                "  <gold>" + usage + "</gold> <dark_gray>-</dark_gray> <gray>" + desc));
    }

    // =========================================================================
    // TabCompleter
    // =========================================================================

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "help", "title", "subtitle", "times", "preview",
            "send", "broadcast", "actionbar", "clear", "reload"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        if (!sender.hasPermission("easytitle.use")) return Collections.emptyList();

        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }

        String sub = args[0].toLowerCase();

        // /etitle send <player|*>  or  /etitle clear <player>
        if ((sub.equals("send") || sub.equals("clear") || sub.equals("actionbar")) && args.length == 2) {
            List<String> names = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            if (!sub.equals("clear")) names.add(0, "*");
            return filterPrefix(names, args[1]);
        }

        // /etitle times <fi> <st> <fo>
        if (sub.equals("times") && args.length <= 4) {
            return filterPrefix(Arrays.asList("10", "20", "40", "60", "70", "80", "100"), args[args.length - 1]);
        }

        return Collections.emptyList();
    }

    // =========================================================================
    // Utilities
    // =========================================================================

    /** Returns (or creates) the staging session for the given sender. */
    private TitleSession getSession(CommandSender sender) {
        UUID uuid = (sender instanceof Player)
                ? ((Player) sender).getUniqueId()
                : TitleSessionManager.CONSOLE_UUID;
        return sessionManager.getOrCreate(uuid);
    }

    /**
     * Joins {@code args} from {@code fromIndex} with spaces.
     */
    private static String joinFrom(String[] args, int fromIndex) {
        return String.join(" ", Arrays.copyOfRange(args, fromIndex, args.length));
    }

    /**
     * Sends a config message (prefixed) to the sender, looked up by {@code key}.
     */
    private void reply(CommandSender sender, String key) {
        replyRaw(sender, plugin.rawMessage(key));
    }

    /**
     * Sends a raw MiniMessage string (prefixed) to the sender.
     */
    private void replyRaw(CommandSender sender, String rawMsg) {
        String full = plugin.rawPrefix() + rawMsg;
        plugin.adventure().sender(sender).sendMessage(mm.deserialize(full));
    }

    private static List<String> filterPrefix(List<String> list, String prefix) {
        String lower = prefix.toLowerCase();
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
