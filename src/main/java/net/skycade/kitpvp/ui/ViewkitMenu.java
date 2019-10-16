package net.skycade.kitpvp.ui;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ViewkitMenu {

    private static final CraftPlayer DUMMY_PLAYER = new CraftPlayer((CraftServer) Bukkit.getServer(), new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(),
            new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())));

    static {
        DUMMY_PLAYER.getHandle().playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NetworkManager(null), DUMMY_PLAYER.getHandle());
    }

    private final Inventory menu;

    public ViewkitMenu(Kit kit) {
        menu = Bukkit.createInventory(null, MenuSize.TWO_LINE.getSize(), "§aView " + kit.getName());
        kit.applyKit(DUMMY_PLAYER);
        addItems(0, getItems(), getPotionEffects(DUMMY_PLAYER.getActivePotionEffects()));
        reset();
        if (kit.getAbilityDesc() != null) {
            menu.setItem(13, new ItemBuilder(Material.PAPER).addLore(kit.getAbilityDesc()).build());
        }
    }

    private void reset() {
        DUMMY_PLAYER.getInventory().clear();
        DUMMY_PLAYER.getInventory().setHelmet(null);
        DUMMY_PLAYER.getInventory().setChestplate(null);
        DUMMY_PLAYER.getInventory().setLeggings(null);
        DUMMY_PLAYER.getInventory().setBoots(null);
        for (PotionEffect effect : DUMMY_PLAYER.getActivePotionEffects()) {
            DUMMY_PLAYER.removePotionEffect(effect.getType());
        }
    }

    private List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : DUMMY_PLAYER.getInventory())
            if (item != null && item.getType() != null)
                items.add(item);
        for (ItemStack item : DUMMY_PLAYER.getInventory().getArmorContents())
            if (item != null && item.getType() != null)
                items.add(item);
        return items;
    }

    private void addItems(int index, List<ItemStack> items, List<PotionEffect> effects) {
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
        });
        return lore;
    }

    private List<PotionEffect> getPotionEffects(Collection<PotionEffect> effects) {
        return new ArrayList<>(effects);
    }

    public void open(Member member) {
        member.getPlayer().openInventory(menu);
    }

}
