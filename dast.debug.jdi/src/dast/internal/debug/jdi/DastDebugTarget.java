package dast.internal.debug.jdi;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;

import dast.debug.DastDebugPlugin;
import dast.model.IDastProject;

/*implements ����IDastDebugTarget�����O
 * ����ɔ���@Override�̈ꕔ���\�b�h���폜
 */

@SuppressWarnings({ "restriction", "unused" })
class DastDebugTarget extends JDIDebugTarget implements IJavaDebugTarget
{

	private IDastProject project;
	private boolean isStarted;
	private boolean isStopped;
	private final EventHandlerFactory eventHandlerFactory;


	DastDebugTarget(final ILaunch launch, final VirtualMachine jvm, final String name,
		      final boolean supportTerminate, final boolean supportDisconnect, final IProcess process,
		      final boolean resume, final IDastProject project)
	  {
		    super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		    this.project = project;
		    this.isStarted = true;
		    this.isStopped = false;
		    this.eventHandlerFactory = new EventHandlerFactory(this);
		  }

	  public boolean isActive()
	  {
	    return isStarted && !isStopped;
	  }


	  public synchronized IDastProject getProject(){
		  return this.project;
	  }
}
