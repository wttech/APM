package com.cognifide.apm.core.history;

import com.cognifide.actions.api.ActionSendException;
import com.cognifide.actions.api.ActionSubmitter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(
    service = RemoteScriptExecutionNotifier.class
)
@Slf4j
public class RemoteScriptExecutionNotifier {

  public static final String REPLICATE_ACTION = "com/cognifide/actions/cqsm/history/replicate";

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  private ActionSubmitter actionSubmitter;

  public void notifyRemoteInstance(Map<String, Object> properties) {
    if (actionSubmitter != null) {
      try {
        log.info("Sending action {} to action submitter", RemoteScriptExecutionNotifier.REPLICATE_ACTION);
        actionSubmitter.sendAction(REPLICATE_ACTION, properties);
        log.info("Action {} was sent to action submitter", RemoteScriptExecutionNotifier.REPLICATE_ACTION);
      } catch (ActionSendException e) {
        log.warn("Cannot send action", e);
      }
    }
  }
}
