package com.cognifide.apm.core.jobs;

import com.cognifide.apm.core.launchers.ScheduledScriptLauncher;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Reference;

public class ScriptUpdateJobConsumer implements JobConsumer {

  public static final String JOB_NAME = "com/cognifide/apm/core/script/update";

  @Reference
  private ScheduledScriptLauncher launcher;

  @Override
  public JobResult process(Job job) {
    launcher.fetchScripts();
    return null;
  }

}
