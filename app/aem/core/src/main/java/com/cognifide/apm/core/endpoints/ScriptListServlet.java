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
package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.utils.ServletUtils;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.Servlet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/list",
				Property.METHOD + "GET",
				Property.DESCRIPTION + "CQSM List Servlet",
				Property.VENDOR
		}
)
public class ScriptListServlet extends SlingAllMethodsServlet {

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {
		List<ScriptModel> files = scriptFinder.findAll(request.getResourceResolver()).stream()
				.map(ScriptModel::new)
        .sorted()
        .collect(Collectors.toList());
    ServletUtils.writeJson(response, files);
  }

	@Getter
	@EqualsAndHashCode
	public static final class ScriptModel implements Comparable<ScriptModel> {

		private static final Comparator<Date> DATE_DESCENDING = Ordering.natural().reverse().nullsLast();
		private static final Comparator<String> STRING_ASCENDING = Ordering.natural().nullsLast();

		private final String fileName;

		private final String author;

		private final boolean executionEnabled;

		private final Date executionSchedule;

		private final String executionMode;

		private final Date lastModified;

		private final boolean valid;

		private final String path;

		public ScriptModel(Script script) {
			this.fileName = FilenameUtils.getName(script.getPath());
			this.author = script.getAuthor();
			this.path = script.getPath();
			this.valid = script.isValid();
			this.lastModified = script.getLastModified();
			this.executionMode = script.getLaunchMode().name().toLowerCase();
			this.executionEnabled = script.isLaunchEnabled();
			this.executionSchedule = script.getLaunchSchedule();
		}

		@Override
		public int compareTo(ScriptModel other) {
			return ComparisonChain.start()
					.compare(this.lastModified, other.lastModified, DATE_DESCENDING)
					.compare(this.fileName, other.fileName, STRING_ASCENDING).result();
		}
	}
}
