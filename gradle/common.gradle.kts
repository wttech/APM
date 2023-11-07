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

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven("https://repo.adobe.com/nexus/content/groups/public")
    }

    afterEvaluate {
        extensions.findByType(PublishingExtension::class)?.apply {
            publications?.configureEach {
                if (this is MavenPublication) {
                    pom {
                        url.set("https://github.com/wttech/APM")
                        organization {
                            name.set("Wunderman Thompson Technology")
                            url.set("https://www.wundermanthompson.com")
                        }
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                name.set("Dominik Przybyl")
                                email.set("dominik.przybyl@wundermanthompson.com")
                                organization.set("Wunderman Thompson Technology")
                                organizationUrl.set("https://www.wundermanthompson.com")
                                roles.set(listOf("architect", "developer"))
                            }
                            developer {
                                name.set("Krystian Panek")
                                email.set("krystian.panek@wundermanthompson.com")
                                organization.set("Wunderman Thompson Technology")
                                organizationUrl.set("https://www.wundermanthompson.com")
                                roles.set(listOf("consultant"))
                            }
                        }
                        scm {
                            connection.set("https://github.com/wttech/APM.git")
                            developerConnection.set("https://github.com/wttech/APM.git")
                            url.set("https://github.com/wttech/APM")
                        }
                    }
                }
            }
            repositories {
                maven {
                    name = "Mvn"
                    url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = findProperty("apm.repo.mvn.username")?.toString()
                        password = findProperty("apm.repo.mvn.password")?.toString()
                    }
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                }
            }
        }

        extensions.findByType(SigningExtension::class)?.apply {
            val signingKey: String? by project
            val signingPassword: String? by project
            if (signingKey != null && signingPassword != null) {
                useInMemoryPgpKeys(signingKey, signingPassword)
            } else {
                useGpgCmd()
            }
            extensions.findByType(PublishingExtension::class)?.publications?.configureEach {
                val publication = this
                sign(publication)
            }
        }
    }
}