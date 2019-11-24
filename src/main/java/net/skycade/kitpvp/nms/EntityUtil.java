package net.skycade.kitpvp.nms;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class EntityUtil {

    public static boolean registerEntity(Class<? extends Entity> entity, int id, String name) {
        Field cFieldEntityTypes = setFieldAccessible(EntityTypes.class, "c");
        if (cFieldEntityTypes == null) {
            return false;
        }

        Field dFieldEntityTypes = setFieldAccessible(EntityTypes.class, "d");
        if (dFieldEntityTypes == null) {
            return false;
        }

        Field fFieldEntityTypes = setFieldAccessible(EntityTypes.class, "f");
        if (fFieldEntityTypes == null) {
            return false;
        }

        try {
            ((Map)cFieldEntityTypes.get(null)).put(name, entity);
            ((Map)dFieldEntityTypes.get(null)).put(entity, name);
            ((Map)fFieldEntityTypes.get(null)).put(entity, id);
            return true;
        } catch (IllegalAccessException var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public static <T extends Entity> T spawnCustomEntity(Class<T> entity, Location location) {
        try {
            World world = ((CraftWorld)location.getWorld()).getHandle();
            T customEntity = entity.getDeclaredConstructor(World.class).newInstance(world);
            customEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            world.addEntity(customEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return customEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field setFieldAccessible(Class<?> clazz, String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
