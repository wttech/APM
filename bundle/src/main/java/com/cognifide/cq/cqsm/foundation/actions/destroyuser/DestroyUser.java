package com.cognifide.cq.cqsm.foundation.actions.destroyuser;

import java.util.Collections;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.User;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.utils.AuthorizablesUtils;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.actions.CompositeActionResult;
import com.cognifide.cq.cqsm.foundation.actions.purge.Purge;
import com.cognifide.cq.cqsm.foundation.actions.removeuser.RemoveUser;

public class DestroyUser implements Action {

	private final Action purge;

	private final Action remove;

	private final String userId;

	public DestroyUser(String userId) {
		this.purge = new Purge("/");
		this.userId = userId;
		remove = new RemoveUser(Collections.singletonList(userId));
	}

	@Override
	public ActionResult simulate(Context context) throws ActionExecutionException {
		ActionResult actionResult = new ActionResult();
		try {
			User user = AuthorizablesUtils.getUser(context, userId);
			context.setCurrentAuthorizable(user);
			purge.simulate(context).logError(actionResult);
			remove.simulate(context).logError(actionResult);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
		}
		return actionResult;
	}

	@Override
	public ActionResult execute(Context context) throws ActionExecutionException {
		ActionResult actionResult;
		try {
			User user = AuthorizablesUtils.getUser(context, userId);
			context.setCurrentAuthorizable(user);
			ActionResult purgeResult = purge.execute(context);
			ActionResult removeResult = remove.execute(context);
			actionResult = new CompositeActionResult(purgeResult, removeResult);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult = new ActionResult();
			actionResult.logError(MessagingUtils.createMessage(e));
		}
		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}
}
