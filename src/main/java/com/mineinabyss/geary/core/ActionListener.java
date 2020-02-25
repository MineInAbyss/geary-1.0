package com.mineinabyss.geary.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mineinabyss.geary.core.ItemUtil.ItemDegradedException;
import com.mineinabyss.geary.ecs.components.Position;
import com.mineinabyss.geary.ecs.components.ProjectileHitGround;
import com.mineinabyss.geary.ecs.components.Remove;
import com.mineinabyss.geary.ecs.components.control.ActivateOnPhysicalHit;
import com.mineinabyss.geary.ecs.components.control.Activated;
import com.mineinabyss.geary.ecs.components.control.RetainOriginalProperties;
import com.mineinabyss.geary.ecs.components.equipment.Degrading;
import java.util.Optional;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class ActionListener implements Listener {

  private ComponentMapper<ActivateOnPhysicalHit> activateOnPhysicalHitComponentMapper = ComponentMapper
      .getFor(ActivateOnPhysicalHit.class);
  private ComponentMapper<RetainOriginalProperties> retainOriginalPropertiesComponentMapper = ComponentMapper
      .getFor(RetainOriginalProperties.class);
  private final ProjectileMapper projectileMapper;
  private final ItemUtil itemUtil;

  public ActionListener(ProjectileMapper projectileMapper, ItemUtil itemUtil) {
    this.projectileMapper = projectileMapper;
    this.itemUtil = itemUtil;
  }

  @EventHandler
  public void onPlayerAct(PlayerInteractEvent event) {
    switch (event.getAction()) {
      case LEFT_CLICK_AIR:
      case LEFT_CLICK_BLOCK:
        Player player = event.getPlayer();
        Optional<Entity> mainHand = itemUtil.removeOrGet(event.getItem(),
            player.getInventory());

        mainHand.ifPresent(entity -> {
          Location hitLoc = player.getEyeLocation().clone()
              .add(player.getEyeLocation().getDirection().multiply(5.5));

          if (activateOnPhysicalHitComponentMapper.has(entity)) {
            Optional<Location> validHit = Optional.empty();
            RayTraceResult result = player.getEyeLocation().getWorld().rayTrace(
                player.getEyeLocation(), player.getEyeLocation().getDirection(), 5.5,
                FluidCollisionMode.NEVER, false, .2, entity1 -> !entity1.equals(player));

            if (result != null) {
              validHit = Optional.ofNullable(result.getHitPosition().toLocation(hitLoc.getWorld())
                  .subtract(player.getEyeLocation().getDirection().normalize().multiply(.1)));
            }

            if (!validHit.isPresent()) {
              return;
            }

            hitLoc = validHit.get();
          }
          entity.add(new Activated());
          entity.add(new Position(hitLoc));

          if (retainOriginalPropertiesComponentMapper.has(entity)) {
            int reduction = retainOriginalPropertiesComponentMapper.get(entity)
                .getDurabilityReduction();

            entity.add(new Degrading(reduction));
          } else {
            event.setCancelled(true);
          }
        });
        break;
    }
  }

  @EventHandler
  public void onItemDamaged(EntityDamageEvent event) {
    removeEntityIfNeeded(event);
  }


  private void removeEntityIfNeeded(EntityEvent event) {
    if (event.getEntity() instanceof Item && event.getEntity().isDead()) {
      ItemStack itemStack = ((Item) event.getEntity()).getItemStack();

      try {
        itemUtil
            .getEcsEntityFromItem(itemStack).ifPresent(entity -> entity.add(new Remove()));
      } catch (ItemDegradedException e) {
        // No special handling, item is dying anyway.
      }
    }
  }

  @EventHandler
  public void onProjectileHitEvent(ProjectileHitEvent projectileHitEvent) {
    Entity entity = projectileMapper.getEntity(projectileHitEvent.getEntity());

    if (entity != null) {
      entity.add(new ProjectileHitGround());
      entity.add(new Position(projectileHitEvent.getEntity().getLocation()));
      projectileMapper.removeProjectile(projectileHitEvent.getEntity());
    }
  }
}
