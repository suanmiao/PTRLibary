package me.suanmiao.ptrlistview.header.paddingHeader;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;

import me.suanmiao.ptrlistview.header.IPTRHeader;

/**
 * Created by suanmiao on 15/2/27.
 * padding top style,both list content move with header
 */
public abstract class AbstractPaddingTopHeader implements IPTRHeader {

  protected Context mContext;

  public int currentPaddingTop = 0;
  public static final long RESET_TOTAL_DURATION = 300;
  protected int headerHeight;
  protected ViewGroup headerContainer;

  public AbstractPaddingTopHeader(Context context) {
    mContext = context;
  }

  @Override
  public void onPullCancel() {
    animatePaddingTop(-headerHeight);
  }

  @Override
  public void onRefreshComplete() {
    animatePaddingTop(-headerHeight);
  }

  @Override
  public void onRefreshStart() {
    animatePaddingTop(-headerHeight + getHeaderRefreshingHeight());
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

  protected void animatePaddingTop(int to) {
    ValueAnimator resetAnimator =
        ValueAnimator.ofInt(currentPaddingTop, to).setDuration(RESET_TOTAL_DURATION);
    resetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        currentPaddingTop = (Integer) animation.getAnimatedValue();
        setHeaderPaddingTop(currentPaddingTop);
      }
    });
    resetAnimator.start();
  }

  protected void setHeaderPaddingTop(int paddingTop) {
    if (headerContainer != null) {
      headerContainer.setPadding(headerContainer.getPaddingLeft(), paddingTop,
          headerContainer.getPaddingRight(), headerContainer.getPaddingBottom());
    }
  }

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    headerHeight = measuredHeight;
  }

}
