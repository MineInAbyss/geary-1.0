package com.mineinabyss.geary.ecs.entity.migration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.mineinabyss.geary.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.Test;

public class GearyEntityMigrationTest {

  private final NamespacedKey versionKey = NamespacedKeyCreator.createKey("geary", "version");


  @Test
  public void applyFixes() {
    NamespacedKey testKey = NamespacedKeyCreator.createKey("geary", "test");
    GearyEntityMigration gearyEntityMigration = new GearyEntityMigration(versionKey, 1, 7) {
      @Override
      protected void applyFixes(PersistentDataContainer container) {
        container.set(testKey, PersistentDataType.STRING, "set");
      }
    };
    PersistentDataContainer container = mock(PersistentDataContainer.class);

    gearyEntityMigration.apply(container);

    verify(container).set(versionKey, PersistentDataType.LONG, 7L);
    verify(container).set(testKey, PersistentDataType.STRING, "set");
  }
}