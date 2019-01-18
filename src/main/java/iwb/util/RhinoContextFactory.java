package iwb.util;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class RhinoContextFactory extends ContextFactory {
  // Custom {@link Context} to store execution time.
  private static class MyContext extends Context {
    long startTime;
  }

  static {
    // Initialize GlobalFactory with custom factory
    ContextFactory.initGlobal(new RhinoContextFactory());
  }

  // Override {@link #makeContext()}
  protected Context makeContext() {
    MyContext cx = new MyContext();
    // Make Rhino runtime to call observeInstructionCount
    // each 10000 bytecode instructions
    cx.setInstructionObserverThreshold(10000);
    return cx;
  }

  // Override {@link #hasFeature(Context, int)}
  public boolean hasFeature(Context cx, int featureIndex) {
    // Turn on maximum compatibility with MSIE scripts
    switch (featureIndex) {
      case 1:
        return true;

      case 2:
        return true;

      case 3:
        return true;

      case 5:
        return false;
    }
    return super.hasFeature(cx, featureIndex);
  }

  // Override {@link #observeInstructionCount(Context, int)}
  protected void observeInstructionCount(Context cx, int instructionCount) {
    MyContext mcx = (MyContext) cx;
    long currentTime = System.currentTimeMillis();
    if (currentTime - mcx.startTime > 10 * 1000) {
      // More then 10 seconds from Context creation time:
      // it is time to stop the script.
      // Throw Error instance to ensure that script will never
      // get control back through catch or finally.
      throw new Error();
    }
  }

  /* Override {@link #doTopCall(Callable,
  Context, Scriptable,
  Scriptable, Object[])}*/
  protected Object doTopCall(
      Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    MyContext mcx = (MyContext) cx;
    mcx.startTime = System.currentTimeMillis();

    return super.doTopCall(callable, cx, scope, thisObj, args);
  }
}
