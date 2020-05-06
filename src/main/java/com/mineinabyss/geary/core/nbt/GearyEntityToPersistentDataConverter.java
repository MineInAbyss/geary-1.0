package com.mineinabyss.geary.core.nbt;

import com.mineinabyss.geary.ecs.component.Component;
import com.mineinabyss.geary.ecs.entity.GearyEntity;
import com.mineinabyss.geary.ecs.entity.GearyEntityFactory;
import com.mineinabyss.geary.ecs.entity.migration.GearyEntityMigration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class GearyEntityToPersistentDataConverter {

  private final UUIDType uuidType;
  private final NamespacedKey componentsKey;
  private final NamespacedKey uuidKey;
  private final NamespacedKey versionKey;
  private final GearyEntityFactory gearyEntityFactory;
  private final List<GearyEntityMigration> migrations;
  private final ComponentArrayTagType componentArrayTagType;

  public GearyEntityToPersistentDataConverter(NamespacedKey componentsKey,
      NamespacedKey componentsDataKey,
      NamespacedKey componentKeyListKey,
      NamespacedKey uuidKey,
      NamespacedKey versionKey,
      Function<String, NamespacedKey> keyCreator,
      GearyEntityFactory gearyEntityFactory,
      Logger logger,
      List<GearyEntityMigration> migrations) {
    this.componentsKey = componentsKey;
    this.uuidKey = uuidKey;
    this.versionKey = versionKey;
    this.gearyEntityFactory = gearyEntityFactory;
    this.migrations = migrations;
    this.componentArrayTagType = new ComponentArrayTagType(componentsDataKey, componentKeyListKey,
        componentsKey,
        keyCreator, logger);
    uuidType = new UUIDType();
  }

  public GearyEntity readFromItemStack(ItemStack itemStack, Player player) {
    ItemMeta itemMeta = itemStack.getItemMeta();

    applyMigrations(itemMeta);

    itemStack.setItemMeta(itemMeta);

    GearyEntity gearyEntity = gearyEntityFactory
        .createEntity(itemStack, player, extractVersion(itemMeta), extractOrGenerateUUID(itemMeta));
    initialize(gearyEntity);

    return gearyEntity;
  }

  public GearyEntity readFromEntity(Entity entity) {
    applyMigrations(entity);

    long version = extractVersion(entity);
    GearyEntity gearyEntity = gearyEntityFactory
        .createEntity(entity, version, extractOrGenerateUUID(entity));
    initialize(gearyEntity);

    return gearyEntity;
  }

  public PersistentDataHolder applyToPersistentDataHolder(GearyEntity gearyEntity) {
    Component[] components = gearyEntity.getComponents().toArray(new Component[0]);

    gearyEntity.getDataHolder().getPersistentDataContainer()
        .set(componentsKey, componentArrayTagType, components);

    gearyEntity.getDataHolder().getPersistentDataContainer()
        .set(uuidKey, uuidType, gearyEntity.getUUID());

    gearyEntity.getDataHolder().getPersistentDataContainer()
        .set(versionKey, PersistentDataType.LONG, gearyEntity.getVersion());

    return gearyEntity.getDataHolder();
  }

  /**
   * Attach the set of components to the holder and return the holder. No UUID will be associated.
   */
  public void applyToPersistentDataHolder(Set<Component> components, PersistentDataHolder holder) {
    Component[] componentsArray = components.toArray(new Component[0]);

    holder.getPersistentDataContainer()
        .set(componentsKey, componentArrayTagType, componentsArray);

    holder.getPersistentDataContainer()
        .set(versionKey, PersistentDataType.LONG, GearyEntityFactory.ENTITY_VERSION);
  }

  private void initialize(GearyEntity gearyEntity) {
    Component[] components = gearyEntity.getDataHolder().getPersistentDataContainer()
        .get(componentsKey, componentArrayTagType);

    for (Component component : components) {
      gearyEntity.addComponent(component);
    }
  }

  /**
   * Apply migrations to this data holder.
   */
  private void applyMigrations(PersistentDataHolder holder) {
    for (GearyEntityMigration migration : migrations) {
      long version = extractVersion(holder);

      if (migration.getTargetLessThan() > version) {
        migration.apply(holder.getPersistentDataContainer());
      }
    }
  }

  private UUID extractOrGenerateUUID(PersistentDataHolder persistentDataHolder) {
    UUID uuid = persistentDataHolder.getPersistentDataContainer().get(uuidKey, uuidType);
    return uuid == null ? UUID.randomUUID() : uuid;
  }

  private long extractVersion(PersistentDataHolder persistentDataContainer) {
    Long version = persistentDataContainer.getPersistentDataContainer()
        .get(versionKey, PersistentDataType.LONG);
    return version != null ? version : 0;
  }
}
