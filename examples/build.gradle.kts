/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

description = "AEM Permission Management :: Examples"

aem {
    `package` {
        validator {
            enabled.set(false)
        }
    }
    tasks {
        packageCompose {
            vaultDefinition {
                version.set(rootProject.version as String)
                property("installhook.apm.class", "com.cognifide.apm.core.tools.ApmInstallHook")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("apm") {
            artifact(common.publicationArtifact("packageCompose"))
            afterEvaluate {
                artifactId = "apm-examples"
                version = rootProject.version
            }
        }
    }
}