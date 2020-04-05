package com.mineinabyss.geary.ecs.engines;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mineinabyss.geary.core.nbt.GearyEntityToPersistentDataConverter;
import com.mineinabyss.geary.ecs.GearyEngine;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.system.GearySystem;
import com.mineinabyss.geary.ecs.system.GearySystemImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class SimpleGearyEngine implements GearyEngine {

  private Map<Class<? extends GearySystem>, GearySystem> initializingSystems;
  private Map<Class<? extends Plugin>, Map<Class<? extends GearySystem>, GearySystem>> systems;
  private Map<Class<? extends GearySystem>, GearySystem> cleanupSystems;
  private NamespacedKey componentKey;
  private GearyEntityToPersistentDataConverter converter;
  private boolean updateInProcess;
  private Map<UUID, GearyEntity> entities;
  private Logger logger;

  public SimpleGearyEngine(NamespacedKey componentKey,
      GearyEntityToPersistentDataConverter converter, Logger logger) {
    this.componentKey = componentKey;
    this.converter = converter;
    this.logger = logger;
    initializingSystems = Maps.newLinkedHashMap();
    systems = Maps.newLinkedHashMap();
    cleanupSystems = Maps.newLinkedHashMap();
    entities = ImmutableMap.of();
  }

  @Override
  public void update() {
    updateInProcess = true;
    try {
      entities = getEntities();

      initializingSystems.values().forEach(system -> runSystem(system, entities));

      systems.values().stream()
          .map(Map::values)
          .flatMap(Collection::stream)
          .forEach(system -> runSystem(system, entities));

      cleanupSystems.values().forEach(system -> runSystem(system, entities));

      entities.values().forEach(gearyEntity -> {
        converter.applyToPersistentDataHolder(gearyEntity);
        gearyEntity.getItemStack().ifPresent(itemStack ->
            itemStack.setItemMeta((ItemMeta) gearyEntity.getDataHolder()));
      });

    } finally {
      updateInProcess = false;
      entities.clear();
    }
  }

  @Override
  public void addSystem(GearySystem gearySystem, Plugin plugin) {
    systems.computeIfAbsent(plugin.getClass(), aClass -> new LinkedHashMap<>())
        .put(gearySystem.getClass(), gearySystem);

    if (gearySystem instanceof GearySystemImpl) {
      ((GearySystemImpl) gearySystem).setEngine(this);
    }
  }

  public void addCleanupSystem(GearySystem gearySystem) {
    cleanupSystems.put(gearySystem.getClass(), gearySystem);

    if (gearySystem instanceof GearySystemImpl) {
      ((GearySystemImpl) gearySystem).setEngine(this);
    }
  }

  public void addInitializingSystem(GearySystem gearySystem) {
    initializingSystems.put(gearySystem.getClass(), gearySystem);

    if (gearySystem instanceof GearySystemImpl) {
      ((GearySystemImpl) gearySystem).setEngine(this);
    }
  }

  @Override
  public Optional<GearyEntity> getEntity(UUID uuid) {
    Preconditions
        .checkState(updateInProcess,
            "Entities can only be fetched from Systems when the engine is processing.");
    return Optional.ofNullable(entities.get(uuid));
  }

  @Override
  public void addEntity(GearyEntity gearyEntity) {
    Preconditions
        .checkState(updateInProcess,
            "Entities can only be added from Systems when the engine is processing.");

    entities.put(gearyEntity.getUUID(), gearyEntity);
  }

  @Override
  public void removeEntity(GearyEntity gearyEntity) {
    Preconditions
        .checkState(updateInProcess,
            "Entities can only be removed from Systems when the engine is processing.");

    entities.remove(gearyEntity.getUUID(), gearyEntity);
    gearyEntity.getDataHolder().getPersistentDataContainer().remove(componentKey);

    if (gearyEntity.getItemStack().isPresent()) {
      gearyEntity.getItemStack().get().setItemMeta((ItemMeta) gearyEntity.getDataHolder());
    }
  }

  private Map<UUID, GearyEntity> getEntities() {
    HashMap<UUID, GearyEntity> entities = new HashMap<>();

    Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().forEach(itemStack -> {
          if (itemStack != null && itemStack.hasItemMeta() && isGearyEntity(
              itemStack.getItemMeta())) {

            GearyEntity entity = converter.readFromItemStack(itemStack, player);

            entities.put(entity.getUUID(), entity);
          }
        })
    );

    Bukkit.getWorlds().stream().map(World::getEntities).flatMap(Collection::stream)
        .filter(this::isGearyEntity)
        .map(converter::readFromEntity)
        .forEach(gearyEntity -> entities.put(gearyEntity.getUUID(), gearyEntity));

    return entities;
  }

  private boolean isGearyEntity(PersistentDataHolder persistentDataHolder) {
    return persistentDataHolder.getPersistentDataContainer()
        .has(componentKey, PersistentDataType.TAG_CONTAINER);
  }

  private void runSystem(GearySystem system, Map<UUID, GearyEntity> map) {
    system
        .update(map.values().stream().filter(system.getFamily()::matches)
            .collect(toImmutableList()));
  }

  public void removeSystems(Plugin plugin) {
    int numSystems = systems.get(plugin.getClass()).size();
    systems.remove(plugin.getClass());

    if (numSystems > 0) {
      logger.info(
          String.format("Removed %d systems belonging to %s", numSystems, plugin.getName()));
    }
  }
}
