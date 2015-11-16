package dast.debug;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.sun.jdi.VirtualMachine;

import dast.model.IDastProject;
import dast.internal.debug.jdi.JDIDebugFactoryImpl;

public class DastDebugPlugin extends Plugin
{
	  /**
	   * The unique identifier of the plug-in.
	   */
	  public static final String PLUGIN_ID = "daste.debug"; //$NON-NLS-1$
	  /**
	   * The shared instance of the plug-in.
	   */
	  private static DastDebugPlugin plugin;

	  public static IDebugTarget createDebugTarget(final ILaunch launch, final VirtualMachine vm,
	      final String name, final IProcess process, final boolean allowTerminate,
	      final boolean allowDisconnect, final boolean resume, final IDastProject project)
	  {
	    return JDIDebugFactoryImpl.createDebugTarget(launch, vm, name, process, allowTerminate,
	        allowDisconnect, resume, project);
	  }

	  

	  /**
	   * Returns the shared instance of the  core plug-in.
	   * 
	   * @return the shared instance
	   */
	  public static DastDebugPlugin getDefault()
	  {
	    return DastDebugPlugin.plugin;
	  }

	  public static void info(final String message)
	  {
	    DastDebugPlugin.log(IStatus.INFO, message, null);
	  }

	  public static void info(final String message, final Throwable e)
	  {
	    DastDebugPlugin.log(IStatus.INFO, message, e);
	  }

	  /**
	   * Logs a status object to the Eclipse error log.
	   * 
	   * @param status
	   *          the status object to record
	   */
	  public static void log(final IStatus status)
	  {
	    DastDebugPlugin.getDefault().getLog().log(status);
	  }

	  /**
	   * Logs a string to the Eclipse error log as an <code>IStatus.ERROR</code> object.
	   * 
	   * @param message
	   *          the message to be recorded
	   */
	  public static void log(final String message)
	  {
	    DastDebugPlugin.log(new Status(IStatus.ERROR, DastDebugPlugin.PLUGIN_ID, IStatus.ERROR,
	        message, null));
	  }

	  /**
	   * Logs the message associated with a throwable object to the Eclipse error log as an
	   * <code>IStatus.ERROR</code> object.
	   * 
	   * @param e
	   *          the throwable object whose message is recorded
	   */
	  public static void log(final Throwable e)
	  {
	    e.printStackTrace();
	    DastDebugPlugin.log(new Status(IStatus.ERROR, DastDebugPlugin.PLUGIN_ID, IStatus.ERROR, e
	        .getMessage(), e));
	  }

	  public static void warn(final String message)
	  {
	    DastDebugPlugin.log(IStatus.WARNING, message);
	  }

	  public static void warn(final String message, final Throwable e)
	  {
	    DastDebugPlugin.log(IStatus.WARNING, message, e);
	  }

	  private static void log(final int severity, final String message)
	  {
	    DastDebugPlugin.log(severity, message, null);
	  }

	  private static void log(final int severity, final String message, final Throwable e)
	  {
	    DastDebugPlugin.log(new Status(severity, "edu.buffalo.cse..debug.core", severity, message,
	        e));
	  }

	  /**
	   * Constructs the  core plug-in. This constructor is called by the Eclipse platform and should
	   * not be called by clients.
	   * 
	   * @throws IllegalStateException
	   *           if the plug-in has already been instantiated
	   */
	  public DastDebugPlugin()
	  {
	    if (DastDebugPlugin.plugin != null)
	    {
	      // TODO Add log message and internationalize the string literal
	      throw new IllegalStateException("The  core plug-in class already exists.");
	    }
	    //ASTPluginProxy.setOwner(this);
	  }

	  
	  @Override
	  public void start(final BundleContext context) throws Exception
	  {
	    super.start(context);
	    DastDebugPlugin.plugin = this;
	  }

	  @Override
	  public void stop(final BundleContext context) throws Exception
	  {
	    DastDebugPlugin.plugin = null;
	    super.stop(context);
	  }

}
