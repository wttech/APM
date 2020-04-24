import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
    java
}

subprojects {
    if (parent?.name in listOf("main", "extension")) {
        apply(plugin = "java")
        apply(plugin = "kotlin")
        apply(plugin = "maven-publish")

        dependencies {
            implementation("com.adobe.aem:uber-jar:6.3.0:apis")
            implementation("org.osgi:osgi.cmpn:6.0.0")
            implementation("org.osgi:org.osgi.core:6.0.0")
            implementation("org.osgi:org.osgi.service.component.annotations:1.3.0")
            implementation("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
            implementation("org.osgi:org.osgi.annotation:6.0.0")

            implementation("com.google.code.gson:gson:2.3.1")
            implementation("commons-lang:commons-lang:2.5")
            implementation("commons-io:commons-io:2.4")
            implementation("commons-codec:commons-codec:1.5")
            implementation("commons-collections:commons-collections:3.2.1")
            implementation("javax.jcr:jcr:2.0")
            implementation("javax.annotation:javax.annotation-api:1.3.2")
            implementation("javax.inject:javax.inject:1")
            implementation("javax.servlet:jsp-api:2.0")
            implementation("javax.servlet:servlet-api:2.4")
            implementation("org.slf4j:slf4j-log4j12:1.7.7")
        }

        tasks {
            withType<Test>().configureEach {
                failFast = true
                useJUnitPlatform()
                testLogging {
                    events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
                }

                dependencies {
                    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
                    testImplementation("io.wcm:io.wcm.testing.aem-mock.junit5:2.3.2")
                    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
                }
            }

            named("compileJava", JavaCompile::class).configure {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
            }

            named("compileKotlin", KotlinCompile::class).configure {
                kotlinOptions.jvmTarget = "1.8"
            }

            register<org.gradle.jvm.tasks.Jar>("sourcesJar") {
                classifier = "sources"
                from(sourceSets.main.get().allJava)
            }

            register<org.gradle.jvm.tasks.Jar>("javadocJar") {
                classifier = "javadoc"
                from(javadoc.get().destinationDir)
            }
        }

        publishing {
            publications {
                register<MavenPublication>("apm") {
                    from(components["java"])
                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])
                    afterEvaluate {
                        artifactId = "apm-" + project.name
                        version = rootProject.version
                    }
                }
            }
        }
    }
}
