package com.azreyzaako.easytitle.gui;

import com.azreyzaako.easytitle.EasyTitle;
import com.azreyzaako.easytitle.managers.TitleSessionManager;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TitleGui implements Listener {
    private final EasyTitle plugin;
    private final String titleName = ChatColor.GOLD + "EasyTitle Menu";
    /** Tracks players who confirmed an edit — prevents the onClose from reopening the GUI. */
    private final Set<UUID> confirmed = new HashSet<>();

    public TitleGui(EasyTitle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, titleName);
        TitleSessionManager.TitleSession session = plugin.getSessionManager().getOrCreate(player.getUniqueId());

        // Fill background
        ItemStack glass = createItem(XMaterial.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, glass);
        }

        // Row 1: Info & Components
        inv.setItem(10, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Title",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + (session.getTitle().isEmpty() ? "None" : session.getTitle()),
                "", ChatColor.GREEN + "Click to edit!"));
        inv.setItem(11, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Subtitle",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + (session.getSubtitle().isEmpty() ? "None" : session.getSubtitle()),
                "", ChatColor.GREEN + "Click to edit!"));
        inv.setItem(12, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Actionbar",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + (session.getActionbar().isEmpty() ? "None" : session.getActionbar()),
                "", ChatColor.GREEN + "Click to edit!"));
        inv.setItem(13, createItem(XMaterial.JUKEBOX, ChatColor.YELLOW + "Staged Sound",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE.toString() + (session.getSound() == null ? "None" : session.getSound().name()),
                ChatColor.GRAY + "Vol/Pitch:", ChatColor.WHITE.toString() + session.getVolume() + " / " + session.getPitch(),
                "", ChatColor.GREEN + "Click to edit!"));

        // Row 1: Times
        inv.setItem(14, createItem(XMaterial.CLOCK, ChatColor.AQUA + "Fade In",
                ChatColor.GRAY + "Ticks: " + ChatColor.WHITE + session.getFadeIn(), ChatColor.YELLOW + "Click to add 10 (Max 100)"));
        inv.setItem(15, createItem(XMaterial.CLOCK, ChatColor.AQUA + "Stay Time",
                ChatColor.GRAY + "Ticks: " + ChatColor.WHITE + session.getStay(), ChatColor.YELLOW + "Click to add 20 (Max 200)"));
        inv.setItem(16, createItem(XMaterial.CLOCK, ChatColor.AQUA + "Fade Out",
                ChatColor.GRAY + "Ticks: " + ChatColor.WHITE + session.getFadeOut(), ChatColor.YELLOW + "Click to add 10 (Max 100)"));

        // Row 2: Actions
        inv.setItem(20, createItem(XMaterial.ENDER_EYE, ChatColor.GREEN + "Preview", ChatColor.GRAY + "Show to yourself"));
        inv.setItem(22, createItem(XMaterial.BEACON, ChatColor.GOLD + "Broadcast", ChatColor.GRAY + "Send to all players"));
        inv.setItem(24, createItem(XMaterial.BARRIER, ChatColor.RED + "Clear Staging", ChatColor.GRAY + "Reset your staged items"));

        player.openInventory(inv);
    }

    private ItemStack createItem(XMaterial mat, String name, String... lore) {
        ItemStack item = mat.parseItem();
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void openAnvilEditor(Player player, String target, String currentText) {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    Player p = stateSnapshot.getPlayer();
                    // Only re-open main GUI if the player did NOT confirm an edit
                    if (confirmed.remove(p.getUniqueId())) return;
                    plugin.getServer().getScheduler().runTask(plugin, () -> open(p));
                })
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String text = stateSnapshot.getText();
                    if ("none".equalsIgnoreCase(text)) text = "";
                    TitleSessionManager.TitleSession session = plugin.getSessionManager().getOrCreate(stateSnapshot.getPlayer().getUniqueId());
                    
                    if (target.equals("title")) session.setTitle(text);
                    else if (target.equals("subtitle")) session.setSubtitle(text);
                    else if (target.equals("actionbar")) session.setActionbar(text);
                    else if (target.equals("sound")) {
                        java.util.Optional<XSound> opt = XSound.matchXSound(text);
                        if (opt.isPresent()) session.setSound(opt.get(), 1.0f, 1.0f);
                        else {
                            stateSnapshot.getPlayer().sendMessage(ChatColor.RED + "Invalid sound name: " + text);
                            return Collections.emptyList();
                        }
                    }

                    confirmed.add(stateSnapshot.getPlayer().getUniqueId());
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(currentText == null || currentText.isEmpty() ? "none" : currentText)
                .title("Edit " + target)
                .plugin(plugin)
                .open(player);
    }

    @EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.HIGH)
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(titleName)) {
            e.setCancelled(true);
            if (!(e.getWhoClicked() instanceof Player)) return;
            Player p = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            TitleSessionManager.TitleSession session = plugin.getSessionManager().getOrCreate(p.getUniqueId());

            switch (slot) {
                case 10:
                    openAnvilEditor(p, "title", session.getTitle());
                    break;
                case 11:
                    openAnvilEditor(p, "subtitle", session.getSubtitle());
                    break;
                case 12:
                    openAnvilEditor(p, "actionbar", session.getActionbar());
                    break;
                case 13:
                    openAnvilEditor(p, "sound", session.getSound() == null ? "" : session.getSound().name());
                    break;
                case 14: // Fade in
                    int fi = session.getFadeIn() + 10;
                    if (fi > 100) fi = 0;
                    session.setFadeIn(fi);
                    open(p);
                    break;
                case 15: // Stay
                    int st = session.getStay() + 20;
                    if (st > 200) st = 20;
                    session.setStay(st);
                    open(p);
                    break;
                case 16: // Fade out
                    int fo = session.getFadeOut() + 10;
                    if (fo > 100) fo = 0;
                    session.setFadeOut(fo);
                    open(p);
                    break;
                case 20: // Preview
                    p.closeInventory();
                    p.performCommand("etitle preview");
                    break;
                case 22: // Broadcast
                    p.closeInventory();
                    p.performCommand("etitle broadcast");
                    break;
                case 24: // Clear
                    session.setTitle("");
                    session.setSubtitle("");
                    session.setActionbar("");
                    session.setSound(null, 1f, 1f);
                    session.setFadeIn(plugin.defaultFadeIn());
                    session.setStay(plugin.defaultStay());
                    session.setFadeOut(plugin.defaultFadeOut());
                    open(p);
                    break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.HIGH)
    public void onDrag(org.bukkit.event.inventory.InventoryDragEvent e) {
        if (e.getView().getTitle().equals(titleName)) {
            e.setCancelled(true);
        }
    }
}
