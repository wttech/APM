/**
 * Common configuration for all application artifacts
 */
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
    maven("https://repo.adobe.com/nexus/content/groups/public")
    maven("https://dl.bintray.com/acs/releases")
}

plugins.withType(JavaPlugin::class) {
    tasks {
        withType<JavaCompile>().configureEach {
            with(options) {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
                encoding = "UTF-8"
            }
        }

        withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = TestExceptionFormat.SHORT
            }
        }
    }

    dependencies {
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.6.0")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.6.0")
        "testImplementation"("org.mockito:mockito-core:1.9.5")
        "testImplementation"("org.codehaus.groovy:groovy-all:2.5.7")
        "testImplementation"("org.spockframework:spock-core:1.3-groovy-2.5")
    }
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    tasks {
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}