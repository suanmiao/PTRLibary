package me.suanmiao.ptrlistview.header;

import android.view.View;
import android.view.ViewGroup;

import me.suanmiao.ptrlistview.IPullToRefresh;
import me.suanmiao.ptrlistview.PTRListView;

/**
 * Created by suanmiao on 14-11-3.
 */
public interface IPTRHeader extends PTRListView.PullProgressListener {

  /**
   * @return the height of header
   */
  public int getHeaderHeight();

  /**
   * called after header layout is measured ,to give you a exact height of the header ,or if you
   * have a measured height already,you can ignore it
   * 
   * @param measuredHeight the measured height we get after measure
   */

  public void afterHeaderMeasured(int measuredHeight);

  /**
   * 
   * @return the height of header when list view is in refreshing mode
   */
  public int getHeaderRefreshingHeight();

  public int getHeaderCurrentPaddingTop();

  public View getHeaderLayout(ViewGroup container);

  public int onPull(float progress, IPullToRefresh.REFRESH_STATE refreshState, boolean stateChanged);

  public void onPullCancel();

  public void onRefreshStart();

  public void onInit();

  /**
   * @param currentPullDistance the distance in touch position
   * @return ratio to be divided by real distance
   */
  public float getPullRatio(float currentPullDistance);
}
