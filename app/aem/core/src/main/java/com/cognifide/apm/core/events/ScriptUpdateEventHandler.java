package com.cognifide.apm.core.events;

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.jobs.ScriptUpdateJobConsumer;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(
    immediate = true,
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
        EventConstants.EVENT_FILTER + "=(path=/conf/apm/scripts/*)",
        Property.VENDOR
    }
)
public class ScriptUpdateEventHandler implements EventHandler {

  @Reference
  private JobManager jobManager;

  @Override
  public void handleEvent(Event event) {
    jobManager.addJob(ScriptUpdateJobConsumer.JOB_NAME, null);
  }

}

