package com.cognifide.cq.cqsm.foundation.actions.destroyuser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.utils.AuthorizablesUtils;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.actions.CompositeActionResult;
import com.cognifide.cq.cqsm.foundation.actions.purge.Purge;
import com.cognifide.cq.cqsm.foundation.actions.removefromgroup.RemoveFromGroup;
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
		ActionResult actionResult;
		try {
			User user = AuthorizablesUtils.getUser(context, userId);
			context.setCurrentAuthorizable(user);
			Action removeFromGroups = new RemoveFromGroup(getGroups(user));
			ActionResult purgeResult = purge.simulate(context);
			ActionResult removeFromGroupsResult = removeFromGroups.execute(context);
			ActionResult removeResult = remove.simulate(context);
			actionResult = new CompositeActionResult(purgeResult, removeFromGroupsResult, removeResult);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult = new ActionResult();
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
			Action removeFromGroups = new RemoveFromGroup(getGroups(user));
			ActionResult purgeResult = purge.execute(context);
			ActionResult removeFromGroupsResult = removeFromGroups.execute(context);
			ActionResult removeResult = remove.execute(context);
			actionResult = new CompositeActionResult(purgeResult, removeFromGroupsResult, removeResult);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult = new ActionResult();
			actionResult.logError(MessagingUtils.createMessage(e));
		}
		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return true;
	}

	private List<String> getGroups(User user) throws RepositoryException {
		List<String> groups = new ArrayList<>();
		Iterator<Group> groupIterator = user.declaredMemberOf();
		while (groupIterator.hasNext()) {
			Group group = groupIterator.next();
			groups.add(group.getID());
		}
		return groups;
	}
}
