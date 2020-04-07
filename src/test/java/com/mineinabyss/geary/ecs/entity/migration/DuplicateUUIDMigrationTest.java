package com.mineinabyss.geary.ecs.entity.migration;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mineinabyss.geary.NamespacedKeyCreator;
import com.mineinabyss.geary.core.nbt.UUIDType;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.Before;
import org.junit.Test;

public class DuplicateUUIDMigrationTest {

  private DuplicateUUIDMigration migration;
  private NamespacedKey versionKey;
  private NamespacedKey uuidKey;

  @Before
  public void setUp() throws Exception {
    versionKey = NamespacedKeyCreator.createKey("geary", "version");
    uuidKey = NamespacedKeyCreator.createKey("geary", "uuid");
    migration = new DuplicateUUIDMigration(versionKey, uuidKey);
  }

  @Test
  public void getTargetLessThan() {
    assertThat(migration.getTargetLessThan()).isEqualTo(1);
  }

  @Test
  public void getUpgradeVersion() {
    assertThat(migration.getUpgradeVersion()).isEqualTo(1);
  }

  @Test
  public void applyNewUUID() {
    PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);

    migration.applyFixes(persistentDataContainer);

    verify(persistentDataContainer, times(1))
        .set(eq(uuidKey), isA(UUIDType.class), any(UUID.class));
  }
}