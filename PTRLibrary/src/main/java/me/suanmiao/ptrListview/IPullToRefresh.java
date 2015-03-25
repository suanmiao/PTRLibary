package me.suanmiao.ptrlistview;

import me.suanmiao.ptrlistview.footer.IPTRFooter;
import me.suanmiao.ptrlistview.header.IPTRHeader;

/**
 * Created by suanmiao on 14-11-3.
 */
public interface IPullToRefresh {
  static enum STATE {
    RELEASE_TO_REFRESH,
    PULL_TO_REFRESH,
    REFRESHING,
    DONE,
    LOADING
  }

  public STATE getRefreshState();

  public boolean isRefreshEnable();

  public void setRefreshEnable(boolean enable);

  /**
   * called when refresh start
   */
  public void onRefreshStart();


  /**
   * called when refresh complete
   */
  public void onRefreshComplete();

  public void onLoadStart();

  public void onLoadComplete();

  public boolean isLoadEnable();

  public void setLoadEnable(boolean enable);

  public boolean isLoading();

  public void setHeader(IPTRHeader header);

  public void setFooter(IPTRFooter footer);

  public void setContinuousPulling(boolean continous);

  public void setCustomisePullRatioEnable(boolean enable);
}
