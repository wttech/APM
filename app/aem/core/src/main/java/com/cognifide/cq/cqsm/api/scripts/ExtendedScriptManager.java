package com.cognifide.cq.cqsm.api.scripts;

import com.cognifide.apm.api.services.ScriptManager;

public interface ExtendedScriptManager extends ScriptManager {

  /**
   * Get service for monitoring script events
   */
  EventManager getEventManager();
}
