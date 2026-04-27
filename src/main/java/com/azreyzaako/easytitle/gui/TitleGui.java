package com.azreyzaako.easytitle.gui;

import com.azreyzaako.easytitle.EasyTitle;
import com.azreyzaako.easytitle.managers.TitleSessionManager;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
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

public class TitleGui implements Listener {
    private final EasyTitle plugin;
    private final String titleName = ChatColor.GOLD + "EasyTitle Menu";

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

        // Row 1: Info
        inv.setItem(10, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Title",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + toPlain(session.getTitle())));
        inv.setItem(11, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Subtitle",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + toPlain(session.getSubtitle())));
        inv.setItem(12, createItem(XMaterial.NAME_TAG, ChatColor.YELLOW + "Staged Actionbar",
                ChatColor.GRAY + "Currently:", ChatColor.WHITE + toPlain(session.getActionbar())));

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

    private String toPlain(Component c) {
        if (c.equals(Component.empty())) return "None";
        return plugin.getMiniMessage().serialize(c);
    }

    private ItemStack createItem(XMaterial mat, String name, String... lore) {
        ItemStack item = mat.parseItem();
        if (item == null) return null; // Fallback handled by XSeries if material not found
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(titleName)) {
            e.setCancelled(true);
            if (!(e.getWhoClicked() instanceof Player)) return;
            Player p = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            TitleSessionManager.TitleSession session = plugin.getSessionManager().getOrCreate(p.getUniqueId());

            switch (slot) {
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
                    session.setTitle(Component.empty());
                    session.setSubtitle(Component.empty());
                    session.setActionbar(Component.empty());
                    session.setFadeIn(plugin.defaultFadeIn());
                    session.setStay(plugin.defaultStay());
                    session.setFadeOut(plugin.defaultFadeOut());
                    open(p);
                    break;
            }
        }
    }
}
