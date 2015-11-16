package dast.launch;


import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;

import dast.model.IDastProject;



public interface IDastLaunchFactory {

	  public IDastProject createDastProject(final ILaunch launch);

	  public IVMInstall createVMInstall(final IVMInstall subject);

	  public IVMRunner createVMRunner(final String mode, final IVMInstall subject);

}
