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

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://repo.adobe.com/nexus/content/groups/public") }
    }

    plugins.withId("java") {
        tasks.withType<JavaCompile>().configureEach {
            with(options) {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
                encoding = "UTF-8"
            }
        }

        tasks.withType<Test>().configureEach {
            failFast = true
            useJUnitPlatform()
            testLogging {
                events = setOf(TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.SHORT
            }

            dependencies {
                "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.3.2")
                "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.3.2")
                "testImplementation"("io.wcm:io.wcm.testing.aem-mock.junit5:2.3.2")
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}