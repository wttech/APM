package com.cognifide.cq.cqsm.foundation.actions;

import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Status;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class CompositeActionResult extends ActionResult {

	private static final String MISMATCH_MSG = "Cannot create CompositeActionResult, mismatch of authorizables. Found: {} Expected: {}";

	public CompositeActionResult(ActionResult... args) {
		this.authorizable = checkCommonAuthorizable(args);
		this.status = calculateStatus(args);
		this.messages = mergeMessages(args);
	}

	private List<Message> mergeMessages(ActionResult[] args) {
		List<Message> result = new LinkedList<>();
		for (ActionResult actionResult : args) {
			result.addAll(actionResult.getMessages());
		}
		return result;
	}

	private Status calculateStatus(ActionResult[] args) {
		Status result = Status.SUCCESS;
		for (ActionResult actionResult : args) {
			if ((result == Status.SUCCESS && !Status.SUCCESS.equals(actionResult.getStatus())) ||
					(result == Status.WARNING && Status.ERROR.equals(actionResult.getStatus()))) {
				result = actionResult.getStatus();
			}
		}
		return result;
	}

	private String checkCommonAuthorizable(ActionResult[] args) {
		String pattern = args[0].getAuthorizable();
		for (ActionResult actionResult : args) {
			String current = actionResult.getAuthorizable();
			if (current != null && !StringUtils.equals(current, pattern)) {
				String msg = String.format(MISMATCH_MSG, actionResult.getAuthorizable(), pattern);
				throw new IllegalArgumentException();
			}
		}
		return pattern;
	}
}
