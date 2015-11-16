package dast.launch;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/*getLaunchManager()ÇçÌèú */


/**
 * The activator class controls the plug-in life cycle
 */
public class DastLaunchPlugin extends Plugin
{
  /**
   * The mode used to determine if JIVE is enabled if JIVE is enabled for the corresponding launch.
   */
  public static final String DAST_MODE = "dast"; //$NON-NLS-1$
  /**
   * The unique identifier of the plug-in.
   */
  public static final String PLUGIN_ID = "dast.launch"; //$NON-NLS-1$
  public static String VERSION_WARNING = "It is strongly recommended that you use Jive with a Java Virtual Machine version 6 or newer. You are currently running with version %s. Although we try our best to keep the system running smoothly with previous JVM versions, please note that some features may not work properly or may be missing altogether.\n";
  /**
   * 
   * The shared instance of the plug-in.
   */
  private static DastLaunchPlugin plugin;

  /**
   * Returns the shared instance of the JIVE launching plug-in.
   * 
   * @return the shared instance
   */
  public static DastLaunchPlugin getDefault()
  {
    return DastLaunchPlugin.plugin;
  }

  /**
   * Logs a status object to the Eclipse error log.
   * 
   * @param status
   *          the status object to record
   */
  public static void log(final IStatus status)
  {
    DastLaunchPlugin.getDefault().getLog().log(status);
  }

  /**
   * Logs a string to the Eclipse error log as an <code>IStatus.ERROR</code> object.
   * 
   * @param message
   *          the message to be recorded
   */
  public static void log(final String message)
  {
    DastLaunchPlugin.log(new Status(IStatus.ERROR, DastLaunchPlugin.PLUGIN_ID, IStatus.ERROR,
        message, null));
  }

  /**
   * Logs the message assoicated with a throwable object to the Eclipse error log as an
   * <code>IStatus.ERROR</code> object.
   * 
   * @param e
   *          the throwable object whose message is recorded
   */
  public static void log(final Throwable e)
  {
    DastLaunchPlugin.log(new Status(IStatus.ERROR, DastLaunchPlugin.PLUGIN_ID, IStatus.ERROR, e
        .getMessage(), e));
  }

  /**
   * Constructs the JIVE launch plug-in. This constructor is called by the Eclipse platform and
   * should not be called by clients.
   * 
   * @throws IllegalStateException
   *           if the plug-in has already been instantiated
   */
  public DastLaunchPlugin()
  {
    if (DastLaunchPlugin.plugin != null)
    {
      // TODO Add log message and internationalize the string literal
      throw new IllegalStateException("The JIVE launch plug-in class already exists.");
    }
  }

  /*public IDastLaunchManager getLaunchManager()
  {
    return DastLaunchManager.INSTANCE;
  }*/

  @Override
  public void start(final BundleContext context) throws Exception
  {
    super.start(context);
    DastLaunchPlugin.plugin = this;
  }

  @Override
  public void stop(final BundleContext context) throws Exception
  {
    DastLaunchPlugin.plugin = null;
    super.stop(context);
  }
}