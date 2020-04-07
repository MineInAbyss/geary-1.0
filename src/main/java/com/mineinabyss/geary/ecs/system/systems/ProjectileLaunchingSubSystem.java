package com.mineinabyss.geary.ecs.system.systems;

import com.google.common.collect.ImmutableSet;
import com.mineinabyss.geary.ecs.component.components.ProjectileHitComponents;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProjectileLaunchingSubSystem {

  private final GearyEntityFactory gearyEntityFactory;

  public ProjectileLaunchingSubSystem(
      GearyEntityFactory gearyEntityFactory) {
    this.gearyEntityFactory = gearyEntityFactory;
  }

  public GearyEntity launchProjectile(double speed, int projModel, Player player) {
    org.bukkit.entity.Projectile projectile = player.launchProjectile(Snowball.class,
        player.getEyeLocation().getDirection().normalize()
            .multiply(speed));

    net.minecraft.server.v1_15_R1.EntitySnowball entitysnowball = ((CraftSnowball) projectile)
        .getHandle();

    ItemStack itemStack = new ItemStack(Material.SNOWBALL);
    ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(Material.SNOWBALL);
    itemMeta.setCustomModelData(projModel);
    itemStack.setItemMeta(itemMeta);

    entitysnowball.setItem(CraftItemStack.asNMSCopy(itemStack));

    GearyEntity projectileEntity = gearyEntityFactory.createEntity(projectile);

    projectileEntity.addComponent(new ProjectileHitComponents(ImmutableSet::of));

    return projectileEntity;
  }
}
