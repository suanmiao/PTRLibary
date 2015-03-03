package me.suanmiao.ptrlistview.header;

import android.content.Context;

/**
 * Created by suanmiao on 15/2/27.
 */
public abstract class AbstractNormalHeader implements IPTRHeader {

  private Context mContext;

  public int paddingTop = 0;

  public AbstractNormalHeader(Context context) {
    mContext = context;
  }

  @Override
  public void onPullCancel() {
  }

  @Override
  public void onRefreshStart() {
  }

  @Override
  public void onInit() {
  }

  @Override
  public int getHeaderCurrentPaddingTop() {
    return paddingTop;
  }

  public abstract int getHeaderHeight();

  public Context getContext() {
    return mContext;
  }
}
