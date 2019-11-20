package net.skycade.kitpvp.ui;

import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.*;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ViewKitMenu extends DynamicGui {

    private static final ItemStack BACK = new net.skycade.SkycadeCore.utility.ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.YELLOW + "" + ChatColor.GOLD + "Go Back")
            .build();

    private static final CraftPlayer DUMMY_PLAYER = new CraftPlayer((CraftServer) Bukkit.getServer(), new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(),
            new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())));

    static {
        DUMMY_PLAYER.getHandle().playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NetworkManager(null), DUMMY_PLAYER.getHandle());
    }

    public ViewKitMenu(Kit kit) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "View Kit " + kit.getName(), 2);

        kit.applyKit(DUMMY_PLAYER);
        addItems(0, getItems(), getPotionEffects(DUMMY_PLAYER.getActivePotionEffects()));
        reset();
        if (kit.getDescription() != null) {
            List<String> lore = new ArrayList<>(kit.getDescription());
            lore.add("");
            lore.addAll(kit.getHowToObtain());
            setItem(13,  new ItemBuilder(Material.PAPER).setName(ChatColor.GREEN + kit.getName()).addLore(lore).build());
        }

        setItemInteraction(9, new net.skycade.SkycadeCore.utility.ItemBuilder(BACK).build(),
                    (p, ev) -> {
                        new KitMenu(KitPvP.getInstance().getKitManager(), MemberManager.getInstance().getMember(p)).open(p);
                    });
    }

    private void addItems(int index, List<ItemStack> items, List<PotionEffect> effects) {
        for (ItemStack item : items) {
            setItem(index, item);
            index++;
        }
        if (!effects.isEmpty()) {
            org.bukkit.inventory.ItemStack potEffects = new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16456)).setName("§bPotion Effects").addLore(getLore(effects)).build();
            ItemMeta meta = potEffects.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            potEffects.setItemMeta(meta);

            setItem(index, potEffects);
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
}
