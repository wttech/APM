/**
 * Common configuration for AEM application artifacts
 */
import com.cognifide.gradle.aem.AemExtension
import com.cognifide.gradle.aem.bundle.tasks.bundle

group = "com.cognifide.apm"

plugins.withId("com.cognifide.aem.common") {
    configure<AemExtension> {
        `package` {
            appPath.set("/apps/apm")
            commonDir.set(rootProject.file("app/aem/common/package"))
            validator {
                enabled.set(false)
            }
        }
    }
}

plugins.withId("com.cognifide.aem.bundle") {
    tasks {
        withType<Jar> {
            bundle {
                category = "apm"
                vendor = "wttech"
                license = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                bnd("-plugin org.apache.sling.caconfig.bndplugin.ConfigurationClassScannerPlugin")
            }
        }
    }

    dependencies {
        "implementation"("com.adobe.aem:uber-jar:6.4.0:apis")
        "implementation"("org.osgi:osgi.cmpn:6.0.0")
        "implementation"("org.osgi:org.osgi.core:6.0.0")
        "implementation"("javax.jcr:jcr:2.0")
        "implementation"("javax.annotation:javax.annotation-api:1.3.2")
        "implementation"("javax.inject:javax.inject:1")
        "implementation"("javax.servlet:javax.servlet-api:3.1.0")
    }
}
