package me.suanmiao.ptrlistview.header.marginHeader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import me.suanmiao.ptrlistview.IPullToRefresh;
import me.suanmiao.ptrlistview.PTRListView;
import me.suanmiao.ptrlistview.R;

/**
 * Created by suanmiao on 15/3/5.
 */
public class MarginTopHeader extends AbstractMarginTopHeader {
  private RelativeLayout headerLayout;
  private CircleImageView mCircleImageView;
  private MaterialProgressDrawable mProgressDrawable;
  private static final long ARROW_ANIMATION_DURATION = 300;
  private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
  private int measuredHeaderHeight;

  public MarginTopHeader(Context context) {
    super(context);
  }

  @Override
  protected void setHeaderMarginTop(int marginTop) {
    if (headerContainer != null) {
      headerContainer.setTranslationY(marginTop + measuredHeaderHeight);
      headerContainer.setPadding(headerContainer.getPaddingLeft(), 0,
          headerContainer.getPaddingRight(), headerContainer.getPaddingBottom());
    }
  }

  @Override
  public int getHeaderRefreshingHeight() {
    return getContext().getResources().getDimensionPixelSize(
        R.dimen.default_header_refreshing_height);
  }

  @Override
  public View getHeaderContent(ViewGroup headerContainer) {
    if (headerLayout == null) {
      LayoutInflater inflater =
          (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      // remember to inflate with container,or the result view will not have a layout param
      headerLayout = (RelativeLayout) inflater.inflate(
          R.layout.ptr_margin_header_layout, headerContainer, false);
      // mCircleImageView =
      // new
      // CircleImageView(mContext, CIRCLE_BG_LIGHT, mContext.getResources().getDimensionPixelSize(
      // R.dimen.circle_size) / 2);

      mCircleImageView = (CircleImageView) headerLayout.findViewById(R.id.circle_circle_header);
      // RelativeLayout.LayoutParams layoutParams =
      // new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
      // ViewGroup.LayoutParams.WRAP_CONTENT);
      // layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      // headerLayout.addView(mCircleImageView, layoutParams);
      mProgressDrawable = new MaterialProgressDrawable(mContext, headerLayout);
      mProgressDrawable.setBackgroundColor(CIRCLE_BG_LIGHT);
      mProgressDrawable.updateSizes(MaterialProgressDrawable.DEFAULT);
      int red = mContext.getResources().getColor(R.color.refresh_red);
      int yellow = mContext.getResources().getColor(R.color.refresh_yellow);
      int blue = mContext.getResources().getColor(R.color.refresh_blue);
      int green = mContext.getResources().getColor(R.color.refresh_green);
      mProgressDrawable.setColorSchemeColors(red, yellow, blue, green);
      mCircleImageView.setImageDrawable(mProgressDrawable);
    }

    return headerLayout;
  }

  @Override
  public void onPull(float progress, IPullToRefresh.STATE refreshState, boolean stateChanged) {
    switch (refreshState) {
      case RELEASE_TO_REFRESH:
        if (mProgressDrawable.isRunning()) {
          mProgressDrawable.stop();
        }
        mProgressDrawable.showArrow(true);
        mProgressDrawable.setProgressRotation(progress);
        currentMarginTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
        break;
      case PULL_TO_REFRESH:
        mProgressDrawable.stop();
        mProgressDrawable.showArrow(true);
        mProgressDrawable.setAlpha((int) (255 * progress));
        mProgressDrawable
            .setStartEndTrim(0, Math.min(0.8f, (float) Math.max(0.3f, progress - 0.3)));
        mCircleImageView.setScaleX(Math.min(1f, progress));
        mCircleImageView.setScaleY(Math.min(1f, progress));
        mProgressDrawable.setArrowScale(Math.min(1f, progress));
        mProgressDrawable.setProgressRotation(progress);
        currentMarginTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
        break;
      case REFRESHING:
        currentMarginTop = -(headerHeight - getHeaderRefreshingHeight());
        if (stateChanged) {
          mProgressDrawable.start();
        }
        break;
      case DONE:
        mProgressDrawable.showArrow(false);
        mProgressDrawable.stop();
        currentMarginTop = -headerHeight;
        break;
    }
    setHeaderMarginTop(currentMarginTop);
  }

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    super.afterHeaderMeasured(measuredHeight);
    this.measuredHeaderHeight = measuredHeight;
  }

  @Override
  public void onRefreshComplete() {
    startScaleDownAnimation();
  }

  private void startScaleDownAnimation() {
    mCircleImageView.animate().scaleX(0f).scaleY(0f).setDuration(ARROW_ANIMATION_DURATION).start();
  }

  @Override
  public float getPullRatio(float currentPullDistance) {
    return PTRListView.DEFAULT_PULL_RATIO;
  }
}
