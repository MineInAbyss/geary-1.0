package com.mineinabyss.geary.ecs.system.systems;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.mineinabyss.geary.ecs.GearyEngine;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

public class EntityRemovalSystemTest {

  private GearyEngine gearyEngine;
  private GearyEntityFactory gearyEntityFactory;
  private EntityRemovalSystem entityRemovalSystem;

  @Before
  public void setUp() throws Exception {
    gearyEngine = mock(GearyEngine.class);

    gearyEntityFactory = new GearyEntityFactory();
    entityRemovalSystem = new EntityRemovalSystem();
    entityRemovalSystem.setEngine(gearyEngine);
  }

  @Test
  public void removeItemStack() {
    ItemStack itemStack = mock(ItemStack.class);
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(itemStack, UUID.randomUUID(), mock(
        Player.class));

    entityRemovalSystem.update(gearyEntity);

    verify(itemStack).setAmount(0);
  }

  @Test
  public void removeEntity() {
    Entity entity = mock(Entity.class);
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(entity, UUID.randomUUID());

    entityRemovalSystem.update(gearyEntity);

    verify(entity).remove();
  }
}