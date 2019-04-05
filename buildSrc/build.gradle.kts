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

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://repo.adobe.com/nexus/content/groups/public") }
    maven { url = uri("https://plugins.gradle.org/m2") }
    maven { url = uri("https://dl.bintray.com/cognifide/maven-public") }
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:6.2.0")
    implementation("com.moowork.gradle:gradle-node-plugin:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.20")
    implementation("io.franzbecker:gradle-lombok:1.14")
}