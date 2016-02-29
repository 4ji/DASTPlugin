package dast.internal.debug.jdi;

//import java.beans.EventHandler;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;

//import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
//import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.EventDispatcher;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTargetAdapter;
//import org.eclipse.jdt.internal.debug.core.model.JDIDebugTargetAdapter;
//import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;

import dast.debug.DastDebugPlugin;
import dast.model.IDastProject;

/*implements �ｿｽ�ｿｽ�ｿｽ�ｿｽIDastDebugTarget�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽO
 * �ｿｽ�ｿｽ�ｿｽ�ｿｽﾉ費ｿｽ�ｿｽ�ｿｽ@Override�ｿｽﾌ一部�ｿｽ�ｿｽ�ｿｽ\�ｿｽb�ｿｽh�ｿｽ�ｿｽ�ｿｽ尞�
 */

@SuppressWarnings({ "restriction" })
class DastDebugTarget extends JDIDebugTargetAdapter implements IJavaDebugTarget
{

	private IDastProject project;
	private boolean isStarted;
	private boolean isStopped;
	private final EventHandlerFactory eventHandlerFactory;
	private String projectName; 


	DastDebugTarget(final ILaunch launch, final VirtualMachine jvm, final String name,
		      final boolean supportTerminate, final boolean supportDisconnect, final IProcess process,
		      final boolean resume, final IDastProject project, final String projectName)
	  {
		    super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		    this.project = project;
		    this.isStarted = true;
		    this.isStopped = false;
		    this.projectName = projectName;
		    new ThreadDeathHandler();
		    this.eventHandlerFactory = new EventHandlerFactory(this);

		  }

	  public boolean isActive()
	  {
	    return isStarted && !isStopped;
	  }


	  public IDastProject getProject(){
		  return this.project;
	  }
	  
	  public String getProjectName(){
		  return this.projectName;
	  }
	  
	  
	  private final class ThreadDeathHandler extends JDIDebugTargetAdapter.ThreadDeathHandlerAdapter
	  {
	    protected ThreadDeathHandler()
	    {
	      super();
	    }

	    
	    @Override
	    public boolean handleEvent(final Event event, final JDIDebugTarget target,
	        final boolean suspendVote, final EventSet eventSet)
	    {

	      return super.handleEvent(event, target, suspendVote, eventSet);
	    }

	    @Override
	    protected void createRequest()
	    {
	      final EventRequestManager manager = getEventRequestManager();
	      if (manager != null)
	      {
	        try
	        {
	          final EventRequest request = manager.createThreadDeathRequest();
	          /*request.setSuspendPolicy(generateLockEvents() ? EventRequest.SUSPEND_ALL
	              : EventRequest.SUSPEND_EVENT_THREAD);*/
	          request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
	          request.enable();
	          addJDIEventListener(this, request);
	        }
	        catch (final RuntimeException e)
	        {
	          logError(e);
	        }
	      }
	    }
	  }
	  
	  /*private boolean generateLockEvents(){
		  return false;
	  }*/
	
	  @Override
	  public void addJDIEventListener(IJDIEventListener listener,
				EventRequest request) {
		  if(listener instanceof EventHandlerFactory.ClassPrepareHandler){
			  EventDispatcher dispatcher = ((JDIDebugTarget) getDebugTarget())
						.getEventDispatcher();
				if (dispatcher != null) {
					DastDebugPlugin.log("addEventListner start");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dispatcher.addJDIEventListener(listener, request);
					DastDebugPlugin.log("addEventListner end");
				}
		  }else{
			  EventDispatcher dispatcher = ((JDIDebugTarget) getDebugTarget())
						.getEventDispatcher();
				if (dispatcher != null) {
					dispatcher.addJDIEventListener(listener, request);
				}
		  }
			
		}
		
	  @Override
	  public void handleVMStart(final VMStartEvent event)
	  {

		  
		 try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   DastDebugPlugin.log("HundleVMAtart start");
	    super.handleVMStart(event);
	     DastDebugPlugin.log("HundleVMAtart end");
	    
	    
	  }
	  
	@Override
	  public void handleVMDisconnect(final VMDisconnectEvent event)
	  {
		 try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    super.handleVMDisconnect(event);
	  }

	@Override
	  protected JDIThread newThread(final ThreadReference reference)
	  {
	    try
	    {
	      
	      return new DastThread(this, reference);
	    }
	    catch (final ObjectCollectedException exception)
	    {
	      // ObjectCollectionException can be thrown if the thread has already
	      // completed (exited) in the VM.
	    }
	    return null;
	  }
	

	  @Override
	  public void handleVMDeath(final VMDeathEvent event)
	  {
	    try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    super.handleVMDeath(event);
	  }


}
