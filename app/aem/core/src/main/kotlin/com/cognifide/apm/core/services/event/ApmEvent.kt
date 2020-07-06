/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.services.event

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ExecutionMode
import org.osgi.service.event.Event

abstract class ApmEvent {

    abstract fun getTopic(): String

    fun toOsgiEvent(): Event {
        val properties = mutableMapOf<String, Any>()
        fulfillProperties(properties)
        return Event(getTopic(), properties)
    }

    protected open fun fulfillProperties(properties: MutableMap<String, Any>) {}

    abstract class ScriptApmEvent(val script: Script) : ApmEvent() {
        override fun fulfillProperties(properties: MutableMap<String, Any>) {
            super.fulfillProperties(properties)
            properties["script"] = script.path
        }
    }

    abstract class ExecutionModeApmEvent(script: Script, val mode: ExecutionMode) : ScriptApmEvent(script) {
        override fun fulfillProperties(properties: MutableMap<String, Any>) {
            super.fulfillProperties(properties)
            properties["mode"] = mode.toString()
        }
    }

    class ScriptLaunchedEvent(script: Script, mode: ExecutionMode) : ExecutionModeApmEvent(script, mode) {
        override fun getTopic(): String = SCRIPT_LAUNCHED
    }

    class ScriptExecutedEvent(script: Script, mode: ExecutionMode, private val success: Boolean) : ExecutionModeApmEvent(script, mode) {
        override fun getTopic(): String = SCRIPT_EXECUTED

        override fun fulfillProperties(properties: MutableMap<String, Any>) {
            super.fulfillProperties(properties)
            properties["success"] = success
        }
    }

    class InstallHookExecuted(private val hookName: String) : ApmEvent() {
        override fun getTopic(): String = INSTALL_HOOK_EXECUTED

        override fun fulfillProperties(properties: MutableMap<String, Any>) {
            super.fulfillProperties(properties)
            properties["installHookName"] = hookName
        }
    }

    companion object {
        const val SCRIPT_LAUNCHED: String = "com/cognifide/apm/Script/LAUNCHED"
        const val SCRIPT_EXECUTED: String = "com/cognifide/apm/Script/EXECUTED"
        const val INSTALL_HOOK_EXECUTED: String = "com/cognifide/apm/InstallHook/EXECUTED"
    }
}