package com.mineinabyss.geary.core;

import com.mineinabyss.geary.core.nbt.GearyEntityToPersistentDataConverter;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitGround;
import com.mineinabyss.geary.ecs.component.components.Remove;
import com.mineinabyss.geary.ecs.component.components.control.Activated;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

/**
 * Listener that activates/deactivates ECS entities attached to items or projectiles.
 */
public class ActionListener implements Listener {

  private NamespacedKey componentKey;
  private GearyEntityToPersistentDataConverter converter;
  private GearyEntityFactory gearyEntityFactory;

  public ActionListener(NamespacedKey componentKey,
      GearyEntityToPersistentDataConverter converter, GearyEntityFactory gearyEntityFactory) {
    this.componentKey = componentKey;
    this.converter = converter;
    this.gearyEntityFactory = gearyEntityFactory;
  }

  @EventHandler
  public void onPlayerAct(PlayerInteractEvent event) {
    switch (event.getAction()) {
      case LEFT_CLICK_AIR:
      case LEFT_CLICK_BLOCK:
        ItemStack item = event.getItem();
        if (item != null && isGearyEntity(item.getItemMeta())) {
          GearyEntity gearyEntity = converter.readFromItemStack(item, event.getPlayer());
          gearyEntity.addComponent(new Activated());
          PersistentDataHolder persistentDataHolder = converter
              .applyToPersistentDataHolder(gearyEntity);
          item.setItemMeta((ItemMeta) persistentDataHolder);
          event.setCancelled(true);
        }
        break;
    }
  }

  @EventHandler
  public void onProjectileHitEvent(ProjectileHitEvent projectileHitEvent) {
    if (isGearyEntity(projectileHitEvent.getEntity())) {
      GearyEntity gearyEntity = converter.readFromEntity(projectileHitEvent.getEntity());
      gearyEntity.addComponent(new ProjectileHitGround());
      gearyEntity.addComponent(new Remove());

      // Make invisible entity for dead projectile
      if (projectileHitEvent.getEntity().isDead()) {
        Location location = projectileHitEvent.getEntity().getLocation();
        AreaEffectCloud effectCloud = location.getWorld()
            .spawn(location, AreaEffectCloud.class, areaEffectCloud -> {
              areaEffectCloud.setRadius(0);
              areaEffectCloud.setDuration(1000);
              areaEffectCloud.setParticle(Particle.BLOCK_DUST,
                  projectileHitEvent.getHitBlock() == null ? Material.AIR.createBlockData()
                      :
                          projectileHitEvent.getHitBlock().getBlockData());
            });

        GearyEntity newEntity = gearyEntityFactory
            .createEntity(effectCloud, gearyEntity.getVersion(), gearyEntity.getUUID());
        gearyEntity.getComponents().forEach(newEntity::addComponent);
        gearyEntity = newEntity;
      }

      converter.applyToPersistentDataHolder(gearyEntity);
    }
  }

  private boolean isGearyEntity(PersistentDataHolder dataHolder) {
    return dataHolder != null && dataHolder.getPersistentDataContainer()
        .has(componentKey, PersistentDataType.TAG_CONTAINER);
  }
}
