// Generated code from Butter Knife. Do not modify!
package com.workerman.app.ui.widget;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.workerman.app.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WorkerManSelectDialog_ViewBinding implements Unbinder {
  private WorkerManSelectDialog target;

  @UiThread
  public WorkerManSelectDialog_ViewBinding(WorkerManSelectDialog target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WorkerManSelectDialog_ViewBinding(WorkerManSelectDialog target, View source) {
    this.target = target;

    target.txtTitle = Utils.findRequiredViewAsType(source, R.id.txtTitle, "field 'txtTitle'", TextView.class);
    target.imgBtnClose = Utils.findRequiredViewAsType(source, R.id.imgBtnClose, "field 'imgBtnClose'", ImageButton.class);
    target.lyContentBody = Utils.findRequiredViewAsType(source, R.id.lyContentBody, "field 'lyContentBody'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WorkerManSelectDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txtTitle = null;
    target.imgBtnClose = null;
    target.lyContentBody = null;
  }
}
