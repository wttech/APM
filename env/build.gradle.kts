plugins {
    id("com.cognifide.aem.instance.local")
}

aem {
    instance {
        satisfier {
            packages {
                "cq.actions"("https://github.com/Cognifide/APM/releases/download/apm-4.3.0/cq-actions-msg-replication-6.4.0.zip")
            }
        }
    }
}
