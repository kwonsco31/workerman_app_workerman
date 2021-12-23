package com.workerman.app;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Generated;

@Generated("Android Data Binding")
public class DataBinderMapperImpl extends DataBinderMapper {
  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(0);

  static {
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(2);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    result.add(new gun0912.tedimagepicker.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(24);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "showZoom");
      sKeys.put(2, "buttonText");
      sKeys.put(3, "imageCountFormat");
      sKeys.put(4, "buttonGravity");
      sKeys.put(5, "isOpened");
      sKeys.put(6, "album");
      sKeys.put(7, "selectedNumber");
      sKeys.put(8, "buttonDrawableOnly");
      sKeys.put(9, "isAlbumOpened");
      sKeys.put(10, "media");
      sKeys.put(11, "textColor");
      sKeys.put(12, "uri");
      sKeys.put(13, "buttonBackground");
      sKeys.put(14, "mediaCountText");
      sKeys.put(15, "selectedAlbum");
      sKeys.put(16, "showButton");
      sKeys.put(17, "background");
      sKeys.put(18, "isSelected");
      sKeys.put(19, "selectType");
      sKeys.put(20, "buttonTextColor");
      sKeys.put(21, "text");
      sKeys.put(22, "items");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(0);

    static {
    }
  }
}
