plugins {
    id("com.cognifide.aem.instance.local")
}

aem {
    instance {
        satisfier {
            packages {
                "actions.api"("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2@zip")
                "actions.core"("com.cognifide.cq.actions:com.cognifide.cq.actions.core:6.0.2@zip")
                "actions.replication"("com.cognifide.cq.actions:com.cognifide.cq.actions.msg.replication:6.0.2@zip")
            }
        }
    }
}
