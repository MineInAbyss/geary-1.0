package com.mineinabyss.geary;

import org.bukkit.NamespacedKey;

public final class NamespacedKeyCreator {

    /**
     * Creates a key with the given namespace and key.
     */
    @SuppressWarnings("deprecation")
    public static NamespacedKey createKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}
