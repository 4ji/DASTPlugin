package dast.launch.ui.java;

import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;

import dast.launch.DastLaunchPlugin;


public class JavaApplicationLaunchShortcutAdapter extends JavaApplicationLaunchShortcut {
	
	

	  @Override
	  protected ILaunchConfiguration findLaunchConfiguration(final IType type,
	      final ILaunchConfigurationType configType)
	  {
	    // adapt the work done by the superclass
	    final ILaunchConfiguration config = super.findLaunchConfiguration(type, configType);
	    if (config != null)
	    {
	      try
	      {
	        // make sure the launch uses Jive
	        final ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
	        // Initialize the Dast modes
	        final HashSet<String> jiveModes = new HashSet<String>();
	        jiveModes.add(DastLaunchPlugin.DAST_MODE);
	        wc.addModes(jiveModes);
	        // save the launch configuration
	        wc.doSave();
	        // return modified config
	        return config;
	      }
	      catch (final CoreException e)
	      {
	        // cowardly ignore
	      }
	    }
	    return null;
	  }
}
