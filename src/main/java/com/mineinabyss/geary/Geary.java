package com.mineinabyss.geary;

import com.mineinabyss.geary.core.ActionListener;
import com.mineinabyss.geary.core.nbt.GearyEntityToPersistentDataConverter;
import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.engines.SimpleGearyEngine;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import com.mineinabyss.geary.ecs.system.GearySystem;
import com.mineinabyss.geary.ecs.system.systems.DeactivationSystem;
import com.mineinabyss.geary.ecs.system.systems.DegredationSystem;
import com.mineinabyss.geary.ecs.system.systems.EntityRemovalSystem;
import com.mineinabyss.geary.ecs.system.systems.ProjectileCollisionSystem;
import com.mineinabyss.geary.ecs.system.systems.ProjectileLaunchingSubSystem;
import com.mineinabyss.geary.ecs.system.systems.movement.EntityPullingSystem;
import com.mineinabyss.geary.ecs.system.systems.rendering.ItemDisplaySystem;
import com.mineinabyss.geary.ecs.system.systems.rendering.RopeDisplaySystem;
import com.mineinabyss.geary.ecs.system.systems.tools.GrapplingHookDisconnectingSystem;
import com.mineinabyss.geary.ecs.system.systems.tools.GrapplingHookExtendingSystem;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Geary extends JavaPlugin implements GearyService {

  private GearyEntityToPersistentDataConverter converter;
  private GearyEntityFactory gearyEntityFactory;
  private SimpleGearyEngine gearyEngine;

  @Override
  public void onEnable() {
    NamespacedKey componentsKey = new NamespacedKey(this, "components");
    NamespacedKey componentsDataKey = new NamespacedKey(this, "components-data");
    NamespacedKey componentKeyListKey = new NamespacedKey(this, "component-keys");
    NamespacedKey uuidKey = new NamespacedKey(this, "entity-uuid");

    gearyEntityFactory = new GearyEntityFactory();
    converter = new GearyEntityToPersistentDataConverter(
        componentsKey,
        componentsDataKey, uuidKey,
        componentKeyListKey, s -> new NamespacedKey(this, s),
        gearyEntityFactory, getLogger());

    gearyEngine = new SimpleGearyEngine(componentsKey, converter, getLogger());

    gearyEngine.addSystem(
        new GrapplingHookExtendingSystem(new ProjectileLaunchingSubSystem(gearyEntityFactory)),
        this);
    gearyEngine.addSystem(new GrapplingHookDisconnectingSystem(), this);
    gearyEngine.addSystem(new ProjectileCollisionSystem(), this);
    gearyEngine.addSystem(new EntityPullingSystem(), this);
    gearyEngine.addSystem(new RopeDisplaySystem(), this);
    gearyEngine.addSystem(new DegredationSystem(), this);
    gearyEngine.addSystem(new ItemDisplaySystem(), this);
    gearyEngine.addCleanupSystem(new EntityRemovalSystem());
    gearyEngine.addCleanupSystem(new DeactivationSystem());

    Bukkit.getScheduler()
        .scheduleSyncRepeatingTask(this, gearyEngine::update, 1, 1);

    getServer().getPluginManager()
        .registerEvents(new ActionListener(componentsKey, converter, gearyEntityFactory), this);

    getServer().getPluginManager()
        .registerEvents(new PluginDisableListener((SimpleGearyEngine) gearyEngine), this);

    getServer().getServicesManager()
        .register(GearyService.class, this, this, ServicePriority.Highest);
  }

  @Override
  public void onDisable() {
  }

  @Override
  public void attachToItemStack(Set<Component> components, ItemStack itemStack) {
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(itemStack, UUID.randomUUID(), null);
    components.forEach(gearyEntity::addComponent);

    converter.applyToPersistentDataHolder(gearyEntity);

    itemStack.setItemMeta((ItemMeta) gearyEntity.getDataHolder());
  }

  @Override
  public void attachToEntity(Set<Component> components, Entity entity) {
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(entity, UUID.randomUUID());
    components.forEach(gearyEntity::addComponent);
    converter.applyToPersistentDataHolder(gearyEntity);
  }

  @Override
  public void addSystem(GearySystem entitySystem, Plugin plugin) {
    gearyEngine.addSystem(entitySystem, plugin);
  }
}
