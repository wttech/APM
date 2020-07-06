package com.cognifide.apm.core.services.event

interface EventManager {

    fun trigger(event: ApmEvent)
}