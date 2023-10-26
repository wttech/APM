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

package com.cognifide.apm.core.tools;

import static com.cognifide.apm.core.scripts.ScriptFilters.onInstall;
import static com.cognifide.apm.core.scripts.ScriptFilters.onInstallIfModified;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.RunModesProvider;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.services.ModifiedScriptFinder;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.event.ApmEvent;
import com.cognifide.apm.core.services.event.EventManager;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.RepositoryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.fs.config.MetaInf;
import org.apache.jackrabbit.vault.fs.io.ImportOptions;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.jackrabbit.vault.packaging.VaultPackage;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApmInstallHook extends OsgiAwareInstallHook {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApmInstallHook.class);

  @Override
  public void execute(InstallContext context) throws PackageException {
    if (context.getPhase() == InstallContext.Phase.INSTALLED) {
      LaunchEnvironment currentEnvironment = getCurrentEnvironment();
      String currentHook = getCurrentHook(context);

      handleScripts(context, currentEnvironment, currentHook);
    }
  }

  private void handleScripts(InstallContext context, LaunchEnvironment currentEnvironment, String currentHook) throws PackageException {
    ResourceResolverProvider resolverProvider = getService(ResourceResolverProvider.class);
    try {
      onMessage(context, ProgressTrackerListener.Mode.TEXT, "Installing APM scripts...", "");
      LOGGER.info("Installing APM scripts...");
      SlingHelper.operateTraced(resolverProvider, resolver ->
          executeScripts(context, currentEnvironment, currentHook, resolver)
      );
      EventManager eventManager = getService(EventManager.class);
      eventManager.trigger(new ApmEvent.InstallHookExecuted(currentHook));
    } catch (Exception e) {
      throw new PackageException("Could not run scripts", e);
    }
  }

  private void executeScripts(InstallContext context, LaunchEnvironment currentEnvironment, String currentHook, ResourceResolver resolver) throws PackageException, PersistenceException, RepositoryException {
    ScriptManager scriptManager = getService(ScriptManager.class);
    ScriptFinder scriptFinder = getService(ScriptFinder.class);
    ModifiedScriptFinder modifiedScriptFinder = getService(ModifiedScriptFinder.class);
    RunModesProvider runModesProvider = getService(RunModesProvider.class);

    List<Script> scripts = new ArrayList<>();
    scripts.addAll(scriptFinder.findAll(onInstall(currentEnvironment, runModesProvider, currentHook), resolver));
    scripts.addAll(
        modifiedScriptFinder.findAll(
            onInstallIfModified(currentEnvironment, runModesProvider, currentHook), resolver
        )
    );
    for (Script script : scripts) {
      ExecutionResult result = scriptManager.process(script, ExecutionMode.AUTOMATIC_RUN, resolver);
      logStatus(context, script.getPath(), result);
    }
    onMessage(context, ProgressTrackerListener.Mode.TEXT, "APM scripts installed.", "");
    LOGGER.info("APM scripts installed.");
  }

  private String getCurrentHook(InstallContext context) {
    Properties properties = Optional.of(context)
        .map(InstallContext::getPackage)
        .map(VaultPackage::getMetaInf)
        .map(MetaInf::getProperties)
        .orElse(new Properties());
    String hookPropertyKey = properties.entrySet()
        .stream()
        .filter(entry -> StringUtils.equals((String) entry.getValue(), this.getClass().getCanonicalName()))
        .map(entry -> (String) entry.getKey())
        .findFirst()
        .orElse("");

    Pattern hookPattern = Pattern.compile("installhook\\.(\\w+)\\.class");
    Matcher result = hookPattern.matcher(hookPropertyKey);
    return result.matches() ? result.group(1) : "";
  }

  private LaunchEnvironment getCurrentEnvironment() throws PackageException {
    RunModesProvider runModesProvider = getService(RunModesProvider.class);
    return LaunchEnvironment.of(runModesProvider);
  }

  private void logStatus(InstallContext context, String scriptPath, ExecutionResult result) throws PackageException {
    onMessage(context, ProgressTrackerListener.Mode.TEXT, "", scriptPath);
    if (result.isSuccess()) {
      LOGGER.info("Script successfully executed: {}", scriptPath);
    } else {
      PackageException packageException = new PackageException(String.format("Script cannot be executed properly: %s", scriptPath));
      onError(context, ProgressTrackerListener.Mode.TEXT, "", packageException);
      LOGGER.error("", packageException);
      result.getEntries()
          .stream()
          .filter(entry -> entry.getStatus() == Status.ERROR)
          .map(ExecutionResult.Entry::getMessages)
          .flatMap(Collection::stream)
          .forEach(message -> onMessage(context, ProgressTrackerListener.Mode.TEXT, "E", message));
      onMessage(context, ProgressTrackerListener.Mode.TEXT, "APM scripts installed (with errors, check logs!)", "");
      throw packageException;
    }
  }

  private void onMessage(InstallContext context, ProgressTrackerListener.Mode mode, String text1, String text2) {
    Optional.of(context)
        .map(InstallContext::getOptions)
        .map(ImportOptions::getListener)
        .ifPresent(listener -> listener.onMessage(mode, text1, text2));
  }

  private void onError(InstallContext context, ProgressTrackerListener.Mode mode, String text, Exception e) {
    Optional.of(context)
        .map(InstallContext::getOptions)
        .map(ImportOptions::getListener)
        .ifPresent(listener -> listener.onError(mode, text, e));
  }
}
