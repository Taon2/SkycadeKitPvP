package net.skycade.kitpvp.ui;

import com.mojang.authlib.GameProfile;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class ViewkitMenu {

    private static final CraftPlayer DUMMY_PLAYER = new CraftPlayer((CraftServer) Bukkit.getServer(), new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(),
            new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())));
    static {
        DUMMY_PLAYER.getHandle().playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NetworkManager(null), DUMMY_PLAYER.getHandle());
    }

    private final Inventory menu;

    public ViewkitMenu(KitManager kitManager, Kit kit) {
        menu = Bukkit.createInventory(null, MenuSize.TWO_LINE.getSize(), "§aView " + kit.getName());
        kit.applyKit(DUMMY_PLAYER, 1);
        addItems(0, getItems(), getPotionEffects(DUMMY_PLAYER.getActivePotionEffects()), 1);
        reset();
        /* kit.applyKit(DUMMY_PLAYER, 2);
        addItems(9, getItems(), getPotionEffects(DUMMY_PLAYER.getActivePotionEffects()), 2);
        reset();
        kit.applyKit(DUMMY_PLAYER, 3);
        addItems(18, getItems(), getPotionEffects(DUMMY_PLAYER.getActivePotionEffects()), 3);
        reset(); */
        if (kit.getAbilityDesc() != null) {
            menu.setItem(13, new ItemBuilder(Material.PAPER).addLore(kit.getAbilityDesc()).build());
        }
    }

    private void reset() {
        DUMMY_PLAYER.getInventory().clear();
        for (PotionEffect effect : DUMMY_PLAYER.getActivePotionEffects()) {
            DUMMY_PLAYER.removePotionEffect(effect.getType());
        }
    }

    private List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : DUMMY_PLAYER.getInventory())
            if (item != null && item.getType() != null)
                items.add(item);
        /* for (ItemStack piece : DUMMY_PLAYER.getInventory().getArmorContents()) {
            if (piece != null && piece.getType() != Material.AIR)
                items.add(piece);
        } */
        return items;
    }

    private void addItems(int index, List<ItemStack> items, List<PotionEffect> effects, int level) {
        /*menu.setItem(index, new ItemBuilder(Material.EXP_BOTTLE).setName("§5Level " + level).build());
        index++; */

        for (ItemStack item : items) {
            menu.setItem(index, item);
            index++;
        }
        if (!effects.isEmpty()) {
            ItemStack potEffects = new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16456)).setName("§bPotion Effects").addLore(getLore(effects)).build();
            ItemMeta meta = potEffects.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            potEffects.setItemMeta(meta);
            menu.setItem(index, potEffects);
        }
    }

    private List<String> getLore(List<PotionEffect> effects) {
        List<String> lore = new ArrayList<>();
        effects.forEach(effect -> {
            lore.add("§7Type: " + effect.getType().getName().toLowerCase());
            lore.add(effect.getDuration() != Integer.MAX_VALUE ? "§7Duration: " + effect.getDuration() : "§7Duration: perm");
            /* lore.add("§7Level: " + getPotionLevel(Integer.toString(effect.getAmplifier()).substring(0, 1))); */
        });
        return lore;
    }

    private int getPotionLevel(String amplifier) {
        int level = Integer.parseInt(amplifier);
        level++;
        return level;
    }

    private List<PotionEffect> getPotionEffects(Collection<PotionEffect> effects) {
        List<PotionEffect> potEffects = new ArrayList<>();
        potEffects.addAll(effects);
        return potEffects;
    }

    public void open(Member member) {
        member.getPlayer().openInventory(menu);
    }

}
