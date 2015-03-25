package me.suanmiao.ptrlistview;

/**
 * Created by suanmiao on 15/3/4.
 */
public interface PullProgressListener {

  public void onPull(float progress, IPullToRefresh.STATE state,
      boolean stateChanged);
}
