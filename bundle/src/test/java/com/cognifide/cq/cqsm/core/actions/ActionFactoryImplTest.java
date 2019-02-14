/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.cq.cqsm.core.actions;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.foundation.actions.addtogroup.AddToGroup;
import com.cognifide.cq.cqsm.foundation.actions.allow.Allow;
import com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizable;
import com.cognifide.cq.cqsm.foundation.actions.deny.Deny;
import com.cognifide.cq.cqsm.foundation.actions.save.Save;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

@Ignore // TODO: FixThis - all tests in this class are causing stack overflow exception !!!
public class ActionFactoryImplTest {

	ActionFactoryImpl factory;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() throws ActionCreationException {
		factory = Mockito.mock(ActionFactoryImpl.class);

		when(factory.evaluate(anyString())).thenCallRealMethod();
//		when(factory.getMappers()).thenReturn(new ArrayList<Object>(Arrays.asList(
//				new AddToGroupMapper(),
//				new AllowMapper(),
//				new DenyMapper(),
//				new CreateUserMapper(),
//				new CreateSystemUserMapper(),
//				new CreateGroupMapper(),
//				new SaveMapper()
//		)));
	}

	@Test
	public void shouldCreateAddToGroupAction() throws ActionCreationException {
		assertTrue(factory.evaluate("ADD TO GROUP foo").getAction() instanceof AddToGroup);
		assertTrue(factory.evaluate("ADD TO GROUP [bars, spins]").getAction() instanceof AddToGroup);
	}

	@Test
	public void shouldCreateAllowAction() throws ActionCreationException {
		assertTrue(factory.evaluate("ALLOW /test [READ, MODIFY]").getAction() instanceof Allow);
		assertTrue(factory.evaluate("ALLOW /test [READ, MODIFY] IF EXISTS").getAction() instanceof Allow);
	}

	@Test
	public void shouldCreateDenyAction() throws ActionCreationException {
		assertTrue(factory.evaluate("DENY /test [READ, MODIFY]").getAction() instanceof Deny);
		assertTrue(factory.evaluate("DENY /test [READ, MODIFY] IF EXISTS").getAction() instanceof Deny);
	}

	@Test
	public void shouldCreateAuthorizableAction() throws ActionCreationException {
		assertTrue(factory.evaluate("CREATE USER foo").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo qwerty").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo qwerty IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo /home/test").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo /home/test IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo qwerty /home/test").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE USER foo qwerty /home/test IF NOT EXISTS").getAction() instanceof CreateAuthorizable);

		assertTrue(factory.evaluate("CREATE SYSTEM USER goo").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE SYSTEM USER goo IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE SYSTEM USER goo /test").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE SYSTEM USER goo /test IF NOT EXISTS").getAction() instanceof CreateAuthorizable);

		assertTrue(factory.evaluate("CREATE GROUP bars").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE GROUP bars /test").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE GROUP bars /test IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
		assertTrue(factory.evaluate("CREATE GROUP bars IF NOT EXISTS").getAction() instanceof CreateAuthorizable);
	}

	@Test
	public void shouldCreateSaveAction() throws ActionCreationException {
		assertTrue(factory.evaluate("SAVE").getAction() instanceof Save);
	}

	@Test
	public void shouldNotCreateAction() throws ActionCreationException {
		thrown.expect(ActionCreationException.class);
		factory.evaluate("CREATE USER foo IDENTIFIED BY something");
	}
}
