package com.csdy.jzyy.util;



import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.NotNull;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.csdy.jzyy.util.LivingEntityUtil.forceSetAllCandidateHealth;
import static com.csdy.jzyy.util.LivingEntityUtil.setAbsoluteSeveranceHealth;

public class MsUtil {

    //字段回溯
    public static boolean backTrack(Class<?> caller) {
        try {
            Field[] fields = caller.getDeclaredFields();
            for (Field field : fields) {

                // 对炫猫的特殊处理
                //也许能杀
                if ("DATA_ENTITY_REMOVE".equals(field.getName())) {
                    System.out.println("已对猫特殊处理");
                    return true;
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    String typeName = field.getType().getTypeName();
                    field.setAccessible(true);
                    switch (typeName) {
                        case "boolean":
                            field.set(null, Boolean.FALSE);
                            break;
                        case "int":
                            field.set(null, 0);
                            break;
                        case "float":
                            field.set(null, 0.0F);
                            break;
                        case "double":
                            field.set(null, 0.0D);
                            break;
                    }
                }
            }
        } catch (Throwable e) {
            System.err.println("字段回溯失败");
            e.printStackTrace();
        }
        return false;
    }

    public static void superKillEntity(Entity target){
        if(target != null && !(target instanceof Player)) {
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            MinecraftForge.EVENT_BUS.unregister(target);

            Helper.fieldSetField(target, Entity.class, "removalReason", reason, "f_146795_");
            backTrack(target.getClass());
            target.setPosRaw(Double.NaN, Double.NaN, Double.NaN);
            target.getPassengers().forEach(Entity::stopRiding);
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.setBoundingBox(new AABB(0.0D, 0.0D,0.0D, 0.0D, 0.0D, 0.0D));
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            target.setPos(Double.NaN, Double.NaN, Double.NaN);
            target.updateDynamicGameEventListener(DynamicGameEventListener::remove);
            target.canUpdate = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for(String s : living.getTags()) living.removeTag(s);
                living.invalidateCaps();
                setAbsoluteSeveranceHealth(living,0);
                forceSetAllCandidateHealth(living,0);
                living.deathTime = 20;
                living.hurtTime = 20;
            }
            Level level = target.level();
            level.shouldTickDeath(target);
            Set<UUID> newKnownUuids = Sets.newHashSet();
            EntityLookup newAccess = new EntityLookup();
            newAccess.remove(target);
            ((EntityInLevelCallback) Helper.getField(target, Entity.class, "levelCallback", "f_146801_")).onRemove(Entity.RemovalReason.KILLED);
            if (level instanceof ServerLevel surface) {
                newKnownUuids.addAll(surface.entityManager.knownUuids);
                newKnownUuids.remove(target.getUUID());
                EntitySectionStorage entitySectionStorage = surface.entityManager.sectionStorage;
                surface.entityManager.visibleEntityStorage = newAccess;
                surface.entityManager.visibleEntityStorage.remove(target);
                surface.entityManager.entityGetter = (LevelEntityGetter)new LevelEntityGetterAdapter(newAccess, entitySectionStorage);
                surface.entityManager.knownUuids = newKnownUuids;
                surface.entityManager.knownUuids.remove(target);
                surface.entityManager.permanentStorage = new EntityPersistentStorage<>() {

                    @Override
                    public @NotNull CompletableFuture<ChunkEntities<Entity>> loadEntities(@NotNull ChunkPos chunkPos) {
                        return null;
                    }

                    @Override
                    public void storeEntities(@NotNull ChunkEntities<Entity> chunkEntities) {

                    }

                    @Override
                    public void flush(boolean b) {

                    }
                };
                surface.entityTickList = entityTickList;
                surface.entityTickList.remove(target);
                surface.entityTickList.active.clear();
                surface.entityTickList.passive.clear();
                ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet();
                objectOpenHashSet.remove(target);
                surface.navigatingMobs = (Set)objectOpenHashSet;
                surface.navigatingMobs.remove(target);
                surface.entityManager.callbacks.onDestroyed(target);
                surface.entityManager.callbacks.onTickingEnd(target);
                final MinecraftServer server = surface.getServer();
                RegistryAccess.ImmutableRegistryAccess access = (RegistryAccess.ImmutableRegistryAccess) server.registries().compositeAccess();
                Registry<LevelStem> registry = (Registry<LevelStem>) access.registries.get(Registries.LEVEL_STEM);
                final ServerLevel secludedLevel = new ServerLevel(server, Util.backgroundExecutor(), server.storageSource, (ServerLevelData) surface.getLevelData(), surface.dimension(), registry.get(LevelStem.OVERWORLD), server.progressListenerFactory.create(11), surface.isDebug(), surface.getBiomeManager().biomeZoomSeed, Collections.emptyList(), true, surface.getRandomSequences());
                for (ServerPlayer serverPlayer : surface.getPlayers((entity) -> true)) {
                    secludedLevel.addNewPlayer(serverPlayer);
                    secludedLevel.addRespawnedPlayer(serverPlayer);
                    entityTickList.add(serverPlayer);
                    entityTickList.active.put(serverPlayer.getId(), serverPlayer);
                    entityTickList.passive.put(serverPlayer.getId(), serverPlayer);
                }
                server.getServerResources().managers().getCommands().dispatcher = new CommandDispatcher<>(server.getServerResources().managers().getCommands().dispatcher.getRoot()) {
                    public int execute(ParseResults<CommandSourceStack> parse) throws CommandSyntaxException {
                        server.levels = new LinkedHashMap<>();
                        server.levels.put(Level.OVERWORLD, secludedLevel);
                        return super.execute(parse);
                    }
                };
                try {
                    Field[] fields = target.getClass().getDeclaredFields();
                    AccessibleObject.setAccessible(fields, true);

                    for (Field field : fields) {
                        if (field.getType().getName().contains(target.getClass().getName())) Helper.setFieldValue(target.getClass().getDeclaredField(field.getName()), target, null);
                    }
                }
                catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                ((EntityTickList) Helper.getField(surface, ServerLevel.class, "entityTickList", "f_143243_")).forEach(entityTickList::add);
                Helper.fieldSetField(surface, ServerLevel.class, "entityTickList", entityTickList, "f_143243_");
                ((EntityTickList) Helper.getField(surface, ServerLevel.class, "entityTickList", "f_143243_")).remove(target);
                Helper.fieldSetField(surface, ServerLevel.class, "navigatingMobs", entitySectionStorage, "f_143246_");
                ((Set<Mob>) Helper.getField(surface, ServerLevel.class, "navigatingMobs", "f_143246_")).remove(target);
                Helper.fieldSetField(target, Entity.class, "isAddedToWorld", false, "isAddedToWorld");
                PersistentEntitySectionManager<Entity> manager = surface.entityManager;
                if (target.levelCallback instanceof PersistentEntitySectionManager.Callback callback0) {
                    PersistentEntitySectionManager<Entity>.Callback callback = (PersistentEntitySectionManager<Entity>.Callback) callback0;
                    callback.currentSection.remove(callback.entity);
                    entityTickList.active = Int2ObjectMapUtil.getInstance((Int2ObjectLinkedOpenHashMap<Entity>) entityTickList.active).remove(callback.entity.getId()).synchronize();
                    manager.visibleEntityStorage.byUuid.remove(callback.entity.getUUID());
                    manager.visibleEntityStorage.byId = Int2ObjectMapUtil.getInstance((Int2ObjectLinkedOpenHashMap<Entity>) manager.visibleEntityStorage.byId).remove(callback.entity.getId()).synchronize();
                    manager.visibleEntityStorage.remove(target);
                    manager.callbacks.onDestroyed(target);
                    callback.entity.setLevelCallback(EntityInLevelCallback.NULL);
                }
            }
            else if (level instanceof ClientLevel clientLevel) {
                Entity clientEntity = clientLevel.getEntity(target.getId());
                if (clientEntity != null && !(clientEntity instanceof Player)) {
                    clientEntity.remove(reason);
                    clientEntity.setRemoved(reason);
                    clientEntity.isAddedToWorld = false;
                    clientEntity.setInvisible(true);
                    clientLevel.removeEntity(clientEntity.getId(), reason);
                }
            }
        }
    }

}
