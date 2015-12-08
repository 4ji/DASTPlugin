package dast.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;

import dast.debug.DastDebugPlugin;
import dast.launch.DastLaunchPlugin;
import dast.launch.IDastLaunchFactory;
import dast.launch.LaunchFactory;
import dast.model.IDastProject;

@SuppressWarnings("restriction")
public class DastVMDebugger extends StandardVMDebugger{
	 public DastVMDebugger(final IVMInstall vmInstance)
	  {
	    super(vmInstance);
	  }

	  @Override
	  protected IDebugTarget createDebugTarget(final VMRunnerConfiguration config,
	      final ILaunch launch, final int port, final IProcess process, final VirtualMachine vm)
	  {
	    if (vm != null && vm.version() != null && "1.6".compareTo(vm.version()) > 0)
	    {
	      System.err.format(DastLaunchPlugin.VERSION_WARNING, vm.version());
	    }
	    final IDastLaunchFactory factory = LaunchFactory.createFactory(launch.getLaunchConfiguration());
	    final IDastProject project = factory.createDastProject(launch);
	    String projectName = null;
	    try {
			projectName = launch.getLaunchConfiguration().getWorkingCopy()
				  .getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return DastDebugPlugin.createDebugTarget(launch, vm,
	        renderDebugTarget(config.getClassToLaunch(), port), process, true, false,
	        config.isResumeOnStartup(), project, projectName);
	  }
}
