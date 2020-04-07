package com.mineinabyss.geary.ecs.entity.migration;

import com.mineinabyss.geary.core.nbt.UUIDType;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * In unmarked version 0, geary entities created from a recipe would all be created with the same
 * UUID. This migration generates a new UUID for all geary entities.
 * <p>
 * One caveat of this is that any links between entities will break. There should be very few if any
 * existing entities as the problem was noticed quite fast and the plugin was disabled.
 */
public class DuplicateUUIDMigration extends GearyEntityMigration {

    private final UUIDType uuidType;
    private NamespacedKey uuidKey;

    public DuplicateUUIDMigration(NamespacedKey versionKey, NamespacedKey uuidKey) {
        super(versionKey, 1, 1);
        this.uuidKey = uuidKey;
        uuidType = new UUIDType();
    }

    @Override
    protected void applyFixes(PersistentDataContainer container) {
        container.set(uuidKey, uuidType, UUID.randomUUID());
    }
}
