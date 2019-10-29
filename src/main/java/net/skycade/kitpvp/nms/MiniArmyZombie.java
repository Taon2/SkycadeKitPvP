package net.skycade.kitpvp.nms;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

public class MiniArmyZombie extends EntityZombie {
    private EntityLiving owner;

    public MiniArmyZombie(World world) {
        super(world);
    }

    public UUID getOwnerUUID() {
        return owner == null ? null : owner.getUniqueID();
    }

    public EntityLiving getOwner() {
        return owner;
    }

    public void setOwner(LivingEntity owner) {
        this.setOwner(owner == null ? null : ((CraftLivingEntity) owner).getHandle());
    }

    public void setOwner(EntityLiving owner) {
        this.owner = owner;
    }

    @Override
    protected void n() {
        // Movement
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        // Targeting
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true, EntityPlayer.class));
    }

    /**
     * Adaptation of {@link net.minecraft.server.v1_8_R3.PathfinderGoalOwnerHurtByTarget} to support this entity as zombies aren't tameable
     */
    private class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

        private final MiniArmyZombie miniArmyZombie;
        private EntityLiving attacker;
        private int c;

        private PathfinderGoalOwnerHurtByTarget(MiniArmyZombie miniArmyZombie) {
            super(miniArmyZombie, false);

            this.miniArmyZombie = miniArmyZombie;

            this.a(1);
        }

        public boolean a() {
            EntityLiving owner = this.miniArmyZombie.getOwner();

            if (owner == null) {
                return false;
            } else {
                this.attacker = owner.getLastDamager();
                int i = owner.hurtTimestamp;

                return i != this.c && this.a(this.attacker, false) && this.attacker != owner;
            }
        }

        public void c() {
            this.e.setGoalTarget(this.attacker, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);

            EntityLiving entityliving = this.miniArmyZombie.getOwner();
            if (entityliving != null) {
                this.c = hurtTimestamp;
            }

            super.c();
        }

    }

    /**
     * Adaptation of {@link net.minecraft.server.v1_8_R3.PathfinderGoalOwnerHurtTarget} to support this entity as zombies aren't tameable
     */
    private class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

        private final MiniArmyZombie miniArmyZombie;
        private EntityLiving target;
        private int c;

        public PathfinderGoalOwnerHurtTarget(MiniArmyZombie miniArmyZombie) {
            super(miniArmyZombie, false);

            this.miniArmyZombie = miniArmyZombie;

            this.a(1);
        }

        public boolean a() {
            EntityLiving entityliving = this.miniArmyZombie.getOwner();

            if (entityliving == null) {
                return false;
            } else {
                this.target = entityliving.bf();
                int i = entityliving.bg();

                return i != this.c && this.a(this.target, false) && this.target != owner;
            }
        }

        public void c() {
            this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);

            EntityLiving entityliving = this.miniArmyZombie.getOwner();
            if (entityliving != null) {
                this.c = entityliving.bg();
            }

            super.c();
        }
    }

    /**
     * Adaptation of {@link net.minecraft.server.v1_8_R3.PathfinderGoalFollowOwner} to support this entity as zombies aren't tameable
     */
    public class PathfinderGoalFollowOwner extends PathfinderGoal {

        private final MiniArmyZombie miniArmyZombie;
        private EntityLiving owner;
        private World world;
        private double speed;
        private NavigationAbstract navigation;
        private int cooldown;
        private float innerLimit;
        private float outerLimit;
        private float waterSpeed;

        public PathfinderGoalFollowOwner(MiniArmyZombie miniArmyZombie, double speed, float outerLimit, float innerLimit) {
            this.miniArmyZombie = miniArmyZombie;
            this.world = miniArmyZombie.world;
            this.speed = speed;
            this.navigation = miniArmyZombie.getNavigation();
            this.outerLimit = outerLimit;
            this.innerLimit = innerLimit;

            this.a(3);

            if (!(miniArmyZombie.getNavigation() instanceof Navigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean a() {
            EntityLiving owner = this.miniArmyZombie.getOwner();

            if (owner == null) {
                return false;
            }

            if (owner instanceof EntityHuman && ((EntityHuman) owner).isSpectator()) {
                return false;
            }

            if (this.miniArmyZombie.h(owner) < (double) (this.outerLimit * this.outerLimit)) {
                return false;
            }

            this.owner = owner;

            return true;
        }

        public boolean b() {
            return !this.navigation.m() && this.miniArmyZombie.h(this.owner) > (double) (this.innerLimit * this.innerLimit);
        }

        public void c() {
            this.cooldown = 0;
        }

        public void d() {
            this.owner = null;
            this.navigation.n();
        }

        private boolean a(BlockPosition var1) {
            IBlockData blockData = this.world.getType(var1);
            Block block = blockData.getBlock();

            return block == Blocks.AIR || !block.getMaterial().isSolid();
        }

        public void e() {
            this.miniArmyZombie.getControllerLook().a(this.owner, 10.0F, (float) this.miniArmyZombie.bQ());

            if (--this.cooldown <= 0) {
                this.cooldown = 10;

                if (!this.navigation.a(this.owner, this.speed)) {
                    if (!this.miniArmyZombie.cc()) {
                        if (this.miniArmyZombie.h(this.owner) >= 144.0D) {
                            int x = MathHelper.floor(this.owner.locX) - 2;
                            int z = MathHelper.floor(this.owner.locZ) - 2;
                            int y = MathHelper.floor(this.owner.getBoundingBox().b);

                            for (int offX = 0; offX <= 4; ++offX) {
                                for (int offZ = 0; offZ <= 4; ++offZ) {
                                    IBlockData iblockdata = this.world.getType(new BlockPosition(x + offX, y - 1, z + offZ));

                                    boolean f = iblockdata.getBlock().getMaterial().k();

                                    if ((offX < 1 || offZ < 1 || offX > 3 || offZ > 3) && f && this.a(new BlockPosition(x + offX, y, z + offZ)) && this.a(new BlockPosition(x + offX, y + 1, z + offZ))) {
                                        this.miniArmyZombie.setPositionRotation((double) ((float) (x + offX) + 0.5F), (double) y, (double) ((float) (z + offZ) + 0.5F), this.miniArmyZombie.yaw, this.miniArmyZombie.pitch);
                                        this.navigation.n();
                                        return;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

}
