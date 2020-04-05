package com.mineinabyss.geary.core.nbt;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

public class GearyEntityToPersistentDataConverter {

  private final UUIDType uuidType;
  private NamespacedKey componentsKey;
  private NamespacedKey uuidKey;
  private GearyEntityFactory gearyEntityFactory;
  private ComponentArrayTagType componentArrayTagType;

  public GearyEntityToPersistentDataConverter(NamespacedKey componentsKey,
      NamespacedKey componentsDataKey, NamespacedKey componentKeyListKey,
      NamespacedKey uuidKey,
      Function<String, NamespacedKey> keyCreator,
      GearyEntityFactory gearyEntityFactory,
      Logger logger) {
    this.componentsKey = componentsKey;
    this.uuidKey = uuidKey;
    this.gearyEntityFactory = gearyEntityFactory;
    this.componentArrayTagType = new ComponentArrayTagType(componentsDataKey, componentKeyListKey,
        componentsKey,
        keyCreator, logger);
    uuidType = new UUIDType();
  }

  public GearyEntity readFromItemStack(ItemStack itemStack, Player player) {
    GearyEntity gearyEntity = gearyEntityFactory
        .createEntity(itemStack, extractUUID(itemStack.getItemMeta()), player);
    initialize(gearyEntity);

    return gearyEntity;
  }

  public GearyEntity readFromEntity(Entity entity) {
    GearyEntity gearyEntity = gearyEntityFactory.createEntity(entity, extractUUID(entity));
    initialize(gearyEntity);

    return gearyEntity;
  }

  private void initialize(GearyEntity gearyEntity) {
    Component[] components = gearyEntity.getDataHolder().getPersistentDataContainer()
        .get(componentsKey, componentArrayTagType);

    for (Component component : components) {
      gearyEntity.addComponent(component);
    }
  }

  private UUID extractUUID(PersistentDataHolder persistentDataHolder) {
    return persistentDataHolder.getPersistentDataContainer().get(uuidKey, uuidType);
  }

  public PersistentDataHolder applyToPersistentDataHolder(GearyEntity gearyEntity) {
    Component[] components = gearyEntity.getComponents().toArray(new Component[0]);

    gearyEntity.getDataHolder().getPersistentDataContainer()
        .set(componentsKey, componentArrayTagType, components);

    gearyEntity.getDataHolder().getPersistentDataContainer()
        .set(uuidKey, uuidType, gearyEntity.getUUID());

    return gearyEntity.getDataHolder();
  }
}
