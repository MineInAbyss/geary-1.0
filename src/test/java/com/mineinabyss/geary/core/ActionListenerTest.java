package com.mineinabyss.geary.core;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mineinabyss.geary.FakePlayer;
import com.mineinabyss.geary.core.nbt.GearyEntityToPersistentDataConverter;
import com.mineinabyss.geary.ecs.component.components.control.Activated;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;

public class ActionListenerTest {

  private ActionListener actionListener;
  private Plugin plugin;
  private GearyEntityFactory gearyEntityFactory;
  private GearyEntityToPersistentDataConverter converter;
  private NamespacedKey componentKey;

  @Before
  public void setUp() throws Exception {
    plugin = mock(Plugin.class);
    when(plugin.getName()).thenReturn("geary");

    gearyEntityFactory = new GearyEntityFactory();
    converter = mock(GearyEntityToPersistentDataConverter.class);

    componentKey = createKey("component-key");
    actionListener = new ActionListener(componentKey, converter, gearyEntityFactory);
  }

  @Test
  public void playerAct_addActivateComponent_gearyItem() {
    Player player = new FakePlayer("Someone");
    ItemStack itemStack = mock(ItemStack.class);
    ItemMeta itemMeta = mock(ItemMeta.class);
    when(itemStack.getItemMeta()).thenReturn(itemMeta);
    PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);
    when(persistentDataContainer.has(componentKey, PersistentDataType.TAG_CONTAINER))
        .thenReturn(true);
    when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(itemStack, UUID.randomUUID(), player);
    when(converter.readFromItemStack(itemStack, player)).thenReturn(gearyEntity);

    actionListener
        .onPlayerAct(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemStack, null,
            BlockFace.EAST));

    assertThat(gearyEntity.hasComponent(Activated.class)).isTrue();
  }

  @Test
  public void playerAct_addActivateComponent_nonGearyItem() {
    Player player = new FakePlayer("Someone");
    ItemStack itemStack = mock(ItemStack.class);
    ItemMeta itemMeta = mock(ItemMeta.class);
    when(itemStack.getItemMeta()).thenReturn(itemMeta);
    PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);
    when(persistentDataContainer.has(componentKey, PersistentDataType.TAG_CONTAINER))
        .thenReturn(false);
    when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);

    actionListener
        .onPlayerAct(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemStack, null,
            BlockFace.EAST));

    verify(persistentDataContainer, never()).set(any(), any(), any());
  }

  private NamespacedKey createKey(String key) {
    return new NamespacedKey(plugin, key);
  }

}