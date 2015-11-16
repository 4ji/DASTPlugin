package dast.internal.launch.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import dast.launch.IDastLaunchFactory;
import dast.launch.LaunchFactory;

public class JavaApplicationLaunchDelegate extends JavaLaunchDelegate
{
	  @Override
	  public IVMInstall verifyVMInstall(final ILaunchConfiguration configuration) throws CoreException
	  {
	    final IDastLaunchFactory factory = LaunchFactory.createFactory(configuration);
	    return factory.createVMInstall(super.verifyVMInstall(configuration));
	  }
}