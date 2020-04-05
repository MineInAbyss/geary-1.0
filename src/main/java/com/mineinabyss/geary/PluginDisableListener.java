package com.mineinabyss.geary;

import com.mineinabyss.geary.ecs.engines.SimpleGearyEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class PluginDisableListener implements Listener {

  private SimpleGearyEngine gearyEngine;

  public PluginDisableListener(SimpleGearyEngine gearyEngine) {
    this.gearyEngine = gearyEngine;
  }

  @EventHandler
  public void onPluginDisable(PluginDisableEvent pluginDisableEvent) {
    gearyEngine.removeSystems(pluginDisableEvent.getPlugin());
  }
}
