package dast.debug.jdi.adapter;

import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;

public class JDIThreadAdapter extends JDIThread
{
  public JDIThreadAdapter(final JDIDebugTarget target, final ThreadReference thread)
      throws ObjectCollectedException
  {
    super(target, thread);
  }

  protected abstract class StepHandlerAdapter extends JDIThread.StepHandler
  {
    protected StepHandlerAdapter()
    {
    }
  }
}