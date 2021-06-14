/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.activator;

import com.cognifide.apm.core.launchers.StartupScriptLauncher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;

public class Activator implements BundleActivator, SynchronousBundleListener {

  @Override
  public void start(BundleContext context) throws Exception {
    context.addBundleListener(this);
  }

  @Override
  public void stop(BundleContext context) throws Exception {

  }

  @Override
  public void bundleChanged(BundleEvent event) {
    if (event.getType() == BundleEvent.STARTED) {
      BundleContext context = event.getBundle().getBundleContext();
      ServiceReference<StartupScriptLauncher> reference = context.getServiceReference(StartupScriptLauncher.class);
      StartupScriptLauncher startupScriptLauncher = context.getService(reference);
      startupScriptLauncher.process();
    }
  }

}
