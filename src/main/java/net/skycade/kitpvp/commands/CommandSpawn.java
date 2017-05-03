package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.coreclasses.utils.DelayedTeleport;
import net.skycade.kitpvp.duel.Duel;
import net.skycade.kitpvp.kit.KitManager;

public class CommandSpawn extends Command<KitManager> {

    public CommandSpawn(KitManager module) {
        super(module, "Teleport to spawn", Permission.NONE, "spawn", "warp spawn");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (getModule().getSpawnList().contains(member.getUUID())) {
            member.message("You are already warping to §aspawn§7.");
            return;
        }

        for (Duel duel : getModule().getDuels()) {
            if (duel.getPlayers().contains(member.getUUID())) {
                member.message("You can't use this command when you're in a duel.");
                return;
            }
        }

        new DelayedTeleport(member.getPlayer(), getModule().getKitPvP().getSpawnpoint(), 7);
    }
}