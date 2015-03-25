package me.suanmiao.ptrlistview.header;

import android.view.View;
import android.view.ViewGroup;

import me.suanmiao.ptrlistview.IPullToRefresh;

/**
 * Created by suanmiao on 14-11-3.
 */
public interface IPTRHeader {

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

  public View getHeaderContent(ViewGroup headerContainer);

  /**
   * 
   * @param progress total progress of user's touch,progress = touchDistance/headerRefreshHeight
   * @param refreshState current state of PTR
   * @param stateChanged whether the state of PTR changed
   */
  public void onPull(float progress, IPullToRefresh.STATE refreshState,
      boolean stateChanged);

  public void onPullCancel();

  public void onRefreshStart();

  public void onRefreshComplete();

  /**
   *
   * @param headerContainer container of the header,real target that we should operate on
   */
  public void onInit(ViewGroup headerContainer);

  /**
   * @param currentPullDistance the distance in touch position
   * @return ratio to be divided by real distance
   */
  public float getPullRatio(float currentPullDistance);
}
