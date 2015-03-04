package me.suanmiao.ptrlistview;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by suanmiao on 15/3/4.
 */
public class PTRUtil {
  private PTRUtil() {}

  private static final int VISIBLE_SLOP = 30;


  public static void measureHeader(View child) {
    try {
      ViewGroup.LayoutParams p = child.getLayoutParams();
      if (p == null) {
        p = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
            AbsListView.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(p);
      }

      int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
      int lpHeight = p.height;
      int childHeightSpec;
      if (lpHeight > 0) {
        childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
            View.MeasureSpec.EXACTLY);
      } else {
        childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
            View.MeasureSpec.UNSPECIFIED);
      }
      child.measure(childWidthSpec, childHeightSpec);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * to judge whether items take full page
   * if not ,even if last item is visible ,we should not call listener
   *
   * @return whether items take up full page
   */
  public static boolean itemTakeFullPage(ListView list) {
    if (list.getChildCount() > 0) {
      int totalHeight = 0;
      for (int i = 0; i < list.getChildCount(); i++) {
        View child = list.getChildAt(i);
        Rect childRect = new Rect();
        child.getDrawingRect(childRect);
        totalHeight += childRect.height();
      }
      Rect visibleRect = new Rect();
      list.getGlobalVisibleRect(visibleRect);
      return (visibleRect.bottom - visibleRect.top) - totalHeight < VISIBLE_SLOP;
    }
    return false;
  }


}
