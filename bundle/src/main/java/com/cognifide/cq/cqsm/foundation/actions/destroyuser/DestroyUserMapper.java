package com.cognifide.cq.cqsm.foundation.actions.destroyuser;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;

public class DestroyUserMapper extends BasicActionMapper {

	public static final String REFERENCE = "Remove specified users.\n"
			+ "Removed user are no longer listed as any group members.\n"
			+ "Note that no permissions for removed users are cleaned, so after creating a new user with the same id"
			+ " - it will automatically gain those permissions.";

	@Mapping(
			value = {"DESTROY" + DASH + "USER" + SPACE + STRING},
			args = {"userId"},
			reference = REFERENCE
	)
	public Action mapAction(final String userId) throws ActionCreationException {
		return new DestroyUser(userId);
	}
}
