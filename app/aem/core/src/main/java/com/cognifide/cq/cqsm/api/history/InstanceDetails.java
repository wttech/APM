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
package com.cognifide.cq.cqsm.api.history;

import com.google.common.base.Preconditions;
import lombok.Getter;

public class InstanceDetails {

	@Getter
	private final String hostname;

	@Getter
	private final InstanceType instanceType;

	public InstanceDetails(String hostname, InstanceType instanceType) {
		Preconditions.checkArgument(hostname != null);
		Preconditions.checkArgument(instanceType != null);

		this.hostname = hostname;
		this.instanceType = instanceType;
	}

	public enum InstanceType {

		PUBLISH("publish"),
		AUTHOR("author");

		@Getter
		private final String instanceName;

		InstanceType(String instanceName) {
			this.instanceName = instanceName;
		}

		public static InstanceType fromString(String instanceType) {
			Preconditions.checkArgument(instanceType != null);

			InstanceType result = null;
			for (InstanceType type : InstanceType.values()) {
				if (instanceType.equals(type.toString()) || instanceType.equals(type.instanceName)) {
					result = type;
				}
			}
			return result;
		}
	}
}
