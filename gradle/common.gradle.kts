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
                        url.set("https://github.com/Cognifide/APM")
                        organization {
                            name.set("Cognifide")
                            url.set("https://www.cognifide.com")
                        }
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                name.set("Marcin Jędraszczyk")
                                email.set("marcin.jedraszczyk@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("architect", "lead developer"))
                            }
                            developer {
                                name.set("Piotr Marcinkowski")
                                email.set("piotr.marcinkowski@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("developer"))
                            }
                            developer {
                                name.set("Maciej Geisler")
                                email.set("maciej.geisler@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("developer"))
                            }
                            developer {
                                name.set("Mateusz Bloch")
                                email.set("mateusz.bloch@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("tester"))
                            }
                            developer {
                                name.set("Katarzyna Wielgosz")
                                email.set("katarzyna.wielgosz@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("consultant"))
                            }
                            developer {
                                name.set("Marek Krokosiński")
                                email.set("marek.krokosinski@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("consultant"))
                            }
                            developer {
                                name.set("Piotr Wilczyński")
                                email.set("piotr.wilczynski@cognifide.com")
                                organization.set("Cognifide")
                                organizationUrl.set("https://www.cognifide.com")
                                roles.set(listOf("consultant"))
                            }
                        }
                        scm {
                            connection.set("https://github.com/Cognifide/APM.git")
                            developerConnection.set("https://github.com/Cognifide/APM.git")
                            url.set("https://github.com/Cognifide/APM")
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