// Generated code from Butter Knife. Do not modify!
package com.workerman.app.ui.widget;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.workerman.app.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WorkerManDialog_ViewBinding implements Unbinder {
  private WorkerManDialog target;

  @UiThread
  public WorkerManDialog_ViewBinding(WorkerManDialog target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WorkerManDialog_ViewBinding(WorkerManDialog target, View source) {
    this.target = target;

    target.txtMessage = Utils.findRequiredViewAsType(source, R.id.txtMessage, "field 'txtMessage'", TextView.class);
    target.btnLeft = Utils.findRequiredViewAsType(source, R.id.btnLeft, "field 'btnLeft'", Button.class);
    target.btnRight = Utils.findRequiredViewAsType(source, R.id.btnRight, "field 'btnRight'", Button.class);
    target.btnCenter = Utils.findRequiredViewAsType(source, R.id.btnCenter, "field 'btnCenter'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WorkerManDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txtMessage = null;
    target.btnLeft = null;
    target.btnRight = null;
    target.btnCenter = null;
  }
}
