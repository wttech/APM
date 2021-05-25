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
