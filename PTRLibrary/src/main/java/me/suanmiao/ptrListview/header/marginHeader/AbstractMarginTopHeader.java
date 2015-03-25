package me.suanmiao.ptrlistview.header.marginHeader;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;

import me.suanmiao.ptrlistview.header.IPTRHeader;

/**
 * margin top style (like Lollipop PTR style)
 */
public abstract class AbstractMarginTopHeader implements IPTRHeader {

  protected Context mContext;

  public int currentMarginTop = 0;
  public static final long RESET_TOTAL_DURATION = 300;
  protected int headerHeight;
  protected ViewGroup headerContainer;

  public AbstractMarginTopHeader(Context context) {
    mContext = context;
  }

  @Override
  public void onPullCancel() {
    animateMarginTop(-headerHeight);
  }

  @Override
  public void onRefreshComplete() {
    animateMarginTop(-headerHeight);
  }

  @Override
  public void onRefreshStart() {
    animateMarginTop(-headerHeight + getHeaderRefreshingHeight());
  }

  @Override
  public void onInit(ViewGroup headerContainer) {
    this.headerContainer = headerContainer;
  }

  public int getHeaderHeight() {
    return headerHeight;
  }

  public Context getContext() {
    return mContext;
  }

  protected void animateMarginTop(int to) {
    ValueAnimator resetAnimator =
        ValueAnimator.ofInt(currentMarginTop, to).setDuration(RESET_TOTAL_DURATION);
    resetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        currentMarginTop = (Integer) animation.getAnimatedValue();
        setHeaderMarginTop(currentMarginTop);
      }
    });
    resetAnimator.start();
  }

  protected abstract void setHeaderMarginTop(int marginTop);

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    headerHeight = measuredHeight;
  }

}
