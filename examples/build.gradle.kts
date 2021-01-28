/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

group = "com.cognifide.apm"
description = "APM Examples - a bunch of scripts which can be use as a good start point for newcomers."

aem {
    `package` {
        validator {
            enabled.set(false)
        }
    }
    tasks {
        packageCompose {
            vaultDefinition {
                val currentVersion = rootProject.version as String
                version.set(currentVersion)
                description.set(project.description)
                property("installhook.apm.class", "com.cognifide.apm.core.tools.ApmInstallHook")
                property("dependencies", "com.cognifide.apm:apm-ui.apps:" + currentVersion.substringBefore("-SNAPSHOT"))
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("apmCrx") {
            groupId = project.group.toString() + ".crx"
            artifact(common.publicationArtifact("packageCompose"))
            afterEvaluate {
                artifactId = "apm-examples"
                version = rootProject.version
            }
            pom {
                name.set("APM - " + project.name)
                description.set(project.description)
            }
        }
    }
}