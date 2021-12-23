// Generated code from Butter Knife. Do not modify!
package com.workerman.app.ui.activity;

import android.view.View;
import android.webkit.WebView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.workerman.app.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActWebView_ViewBinding implements Unbinder {
  private ActWebView target;

  @UiThread
  public ActWebView_ViewBinding(ActWebView target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ActWebView_ViewBinding(ActWebView target, View source) {
    this.target = target;

    target.mWebView = Utils.findRequiredViewAsType(source, R.id.webview, "field 'mWebView'", WebView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActWebView target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mWebView = null;
  }
}
