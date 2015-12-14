package dast.internal.debug.jdi;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;

import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;

import dast.debug.DastDebugPlugin;
import dast.ui.ObjectManager;
import dast.ui.ReadDast;


@SuppressWarnings("restriction")
final class EventHandlerFactory
{
  private final DastDebugTarget owner;
  private final AccessWatchpointHandler fieldReadHandler;
  private final ModificationWatchpointHandler fieldWriteHandler;
  private final ClassPrepareHandler classPrepareHandler;
  private final MethodEntryHandler methodEntryHandler;
  private final MethodExitHandler methodExitHandler;
  private final ExceptionHandler exceptionHandler;
  private final Set<ReferenceType> classes;

  private  ObjectManager objectManager;
  private  ReadDast layoutDefinition;

  public boolean ready = false;
  EventHandlerFactory(final DastDebugTarget owner)
  {
    this.owner = owner;
    createDastEnvironment();
    this.fieldReadHandler = new AccessWatchpointHandler();
    this.fieldWriteHandler = new ModificationWatchpointHandler();
    
    this.classPrepareHandler = new ClassPrepareHandler();

    this.methodEntryHandler = new MethodEntryHandler();
    this.methodExitHandler = new MethodExitHandler();
    this.exceptionHandler = new ExceptionHandler();
    this.classes = new HashSet<ReferenceType>();


  }

  private void createDastEnvironment(){
	  try {
		  
		  IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		  String projectName = owner.getProjectName();

		  IPath path = root.getLocation();
		  //System.out.println(path);
		  String dastPath = path.toString() + "\\"+ projectName + "\\DASTFile" ;
		  //System.out.println(dastPath);

		  this.layoutDefinition = new ReadDast(new FileInputStream(dastPath));
		 // this.layoutDefinition = new ReadDast(new FileInputStream("E:\\eclipse-rcp-neon3\\eclipse\\runtime-EclipseApplication\\BST\\DASTFile"));
		  this.objectManager = new ObjectManager(layoutDefinition.getClassDefinition());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			this.layoutDefinition = null;
			this.objectManager = null;
			e.printStackTrace();
		}

  }

  private class AccessWatchpointHandler implements IJDIEventListener
  {
    protected AccessWatchpointHandler()
    {
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public synchronized boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
      if (owner.isActive())
      {
    	AccessWatchpointEvent e = (AccessWatchpointEvent)event;
    	if(e.object() != null 
    			&& e.object().getValue(e.field()) != null 
    			&& e.object().getValue(e.field()).type() instanceof ArrayType){
			System.out.println("array");
			objectManager.arrayWrite(e);
			objectManager.draw();
		}

      }
      return true;
    }
  }

  class ClassPrepareHandler implements IJDIEventListener
  {
    final List<EventRequest> fieldRequests = new ArrayList<EventRequest>();

    protected ClassPrepareHandler()
    {
    	
      createRequest();
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public synchronized boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
     //classes.add(((ClassPrepareEvent) event).referenceType());
    	//System.out.println(((ClassPrepareEvent) event).referenceType().toString());
      if(objectManager.isDefinedClass(((ClassPrepareEvent)event).referenceType()) != null){
    	  createFieldRequests(target, ((ClassPrepareEvent) event).referenceType());
    	  return true;
      }
      return false;
    }

    protected void createFieldRequests(final JDIDebugTarget target, final ReferenceType refType)
    {
      final EventRequestManager manager = target.getEventRequestManager();
      if (manager != null)
      {
        try
        {
          // monitor all non-synthetic fields of the prepared classes for reads/writes
          for (final Object o : refType.fields())
          {
            final Field f = (Field) o;
            // Ignore compiler generated fields
            if (!f.isSynthetic() && f.name().indexOf("$") == -1)
            {
              // monitor field reads
              final AccessWatchpointRequest readRequest = manager.createAccessWatchpointRequest(f);
              readRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
              readRequest.enable();
              fieldRequests.add(readRequest);
              target.addJDIEventListener(fieldReadHandler, readRequest);
              // monitor field writes
              final ModificationWatchpointRequest writeRequest = manager
                  .createModificationWatchpointRequest(f);
              writeRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
              writeRequest.enable();
              fieldRequests.add(writeRequest);
              target.addJDIEventListener(fieldWriteHandler, writeRequest);
              ready = true;
            }
          }
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }

    protected void createRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null)
      {
        try
        {
          final ClassPrepareRequest request = manager.createClassPrepareRequest();
          //owner.jdiManager().modelFilter().filter(request);
          request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
          request.enable();
          owner.addJDIEventListener(this, request);
          
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }

   /* protected void removeFieldRequests(final JDIDebugTarget target)
    {
      final EventRequestManager manager = target.getEventRequestManager();
      if (manager != null && !fieldRequests.isEmpty())
      {
        try
        {
          manager.deleteEventRequests(fieldRequests);
          for (final EventRequest request : fieldRequests)
          {
            target.removeJDIEventListener(
                request instanceof AccessWatchpointRequest ? fieldReadHandler : fieldWriteHandler,
                request);
          }
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }*/
  }

  private class ExceptionHandler implements IJDIEventListener
  {
    private ExceptionRequest request;

    // private List<ExceptionRequest> originalRequests = new ArrayList<ExceptionRequest>();
    protected ExceptionHandler()
    {
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
      if (owner.isActive())
      {

      }
      return true;
    }

    protected void createRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null)
      {
        try
        {
          /**
           * NOTE: (@2012-10-11, dlessa): In Juno, the list of exception requests is implemented as
           * an UnmodifiableCollection, therefore, we cannot circumvent the processing order of the
           * exception handlers any longer... Hopefully, this will not affect the semantics of
           * exception handling in Jive.
           */
          // Don't filter ExceptionEvents as they are needed to adjust the call stack
          if (request != null)
          {
            removeRequest();
          }
          request = manager.createExceptionRequest(null, true, true);
          request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
          request.enable();
          owner.addJDIEventListener(this, request);
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }

    protected void removeRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null && request != null)
      {
        try
        {
          owner.removeJDIEventListener(this, request);
          manager.deleteEventRequest(request);
          request = null;
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }
  }

  private class MethodEntryHandler implements IJDIEventListener
  {
    private MethodEntryRequest request;

    protected MethodEntryHandler()
    {
    	//createRequest();
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
      if (owner.isActive())
      {
    	  /*MethodEntryEvent e = (MethodEntryEvent)event;
    	  System.out.println(e.method().name());
    	  if(e.method().name() == "main"){
    		  
    	  }*/
      }
      return true;
    }

    protected void createRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null)
      {
        try
        {
          if (request != null)
          {
            removeRequest();
          }
          request = manager.createMethodEntryRequest();
         // owner.jdiManager().modelFilter().filter(request);
          request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
          request.enable();
          owner.addJDIEventListener(this, request);
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }

    protected void removeRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null && request != null)
      {
        try
        {
          owner.removeJDIEventListener(this, request);
          manager.deleteEventRequest(request);
          request = null;
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }
  }

  private class MethodExitHandler implements IJDIEventListener
  {
    private MethodExitRequest request;

    protected MethodExitHandler()
    {
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
      if (owner.isActive())
      {

      }
      return true;
    }

    protected void createRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null)
      {
        try
        {
          if (request != null)
          {
            removeRequest();
          }
          request = manager.createMethodExitRequest();
          //owner.jdiManager().modelFilter().filter(request);
          request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
          request.enable();
          owner.addJDIEventListener(this, request);
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }

    protected void removeRequest()
    {
      final EventRequestManager manager = owner.getEventRequestManager();
      if (manager != null && request != null)
      {
        try
        {
          owner.removeJDIEventListener(this, request);
          manager.deleteEventRequest(request);
          request = null;
        }
        catch (final RuntimeException e)
        {
          DastDebugPlugin.log(e);
        }
      }
    }
  }

  private class ModificationWatchpointHandler implements IJDIEventListener
  {
    protected ModificationWatchpointHandler()
    {
    }

    @Override
    public void eventSetComplete(final Event event, final JDIDebugTarget target,
        final boolean suspend, final EventSet eventSet)
    {
      // TODO: add support for Eclipse 3.5 event handling
    }

    @Override
    public synchronized boolean handleEvent(final Event event, final JDIDebugTarget target,
        final boolean suspendVote, final EventSet eventSet)
    {
      if (owner.isActive())
      {
    	  ModificationWatchpointEvent e = (ModificationWatchpointEvent)event;
          if(objectManager != null && 
        		  e != null &&
        		  objectManager.classPrepare(e.object().referenceType())){
          	objectManager.fieldWrite(e);
          	objectManager.draw();
          }
      	
      }
      return true;
    }
   
  }

  public boolean handleVMDisconnect(VMDisconnectEvent event) {
	  objectManager.VMDisconnect(event);
	  return true;
	  
  }
}

