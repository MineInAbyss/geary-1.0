package com.mineinabyss.geary.ecs.entity.migration;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Base class for geary entity migrations. These migrations are applied directly to the NBT before
 * loading an entity.
 */
public abstract class GearyEntityMigration {

    private NamespacedKey versionKey;
    private long targetLessThan;
    private long upgradeVersion;

    public GearyEntityMigration(NamespacedKey versionKey, long targetLessThan,
        long upgradeVersion) {
        this.versionKey = versionKey;

        this.targetLessThan = targetLessThan;
        this.upgradeVersion = upgradeVersion;
    }

    public void apply(PersistentDataContainer container) {
        applyFixes(container);
        container.set(versionKey, PersistentDataType.LONG, upgradeVersion);
    }

    protected abstract void applyFixes(PersistentDataContainer container);


    public long getTargetLessThan() {
        return targetLessThan;
    }

    public long getUpgradeVersion() {
        return upgradeVersion;
    }
}
