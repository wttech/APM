package com.cognifide.apm.core.services.version

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import javax.inject.Inject
import javax.inject.Named

@Model(adaptables = [Resource::class])
class ScriptVersionModel @Inject constructor(
        @param:Named("scriptPath") override val scriptPath: String,
        @param:Named("lastChecksum") override val lastChecksum: String? = null
) : ScriptVersion