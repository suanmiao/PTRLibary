package me.suanmiao.ptrexample.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import me.suanmiao.ptrexample.R;
import me.suanmiao.ptrlistview.IPullToRefresh;
import me.suanmiao.ptrlistview.header.paddingHeader.AbstractPaddingTopHeader;

/**
 * Created by suanmiao on 15/2/27.
 */
public class MieHeader extends AbstractPaddingTopHeader {
  private View headerLayout;
  private View headIcon;
  private View bodyIcon;
  private RotateAnimation circleAnimation;

  public MieHeader(Context context) {
    super(context);
  }

  @Override
  public int getHeaderRefreshingHeight() {
    return getContext().getResources().getDimensionPixelSize(R.dimen.header_refresh_height);
  }

  @Override
  public View getHeaderContent(ViewGroup headerContainer) {
    if (headerLayout == null) {
      LayoutInflater inflater =
          (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      headerLayout = inflater.inflate(
          R.layout.mie_header_layout, headerContainer, false);
      headIcon = headerLayout.findViewById(R.id.mie_head);
      bodyIcon = headerLayout.findViewById(R.id.ic_neck);

      circleAnimation = new RotateAnimation(0, 360,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f);
      circleAnimation.setInterpolator(new LinearInterpolator());
      circleAnimation.setDuration(500);
      circleAnimation.setRepeatCount(-1);
    }
    return headerLayout;
  }

  @Override
  public void onPull(float progress, IPullToRefresh.STATE refreshState, boolean changed) {
    switch (refreshState) {
      case RELEASE_TO_REFRESH:
        float originalValue =
            (getHeaderRefreshingHeight() * progress);
        currentPaddingTop = (int) (-getHeaderHeight() + originalValue);

        // float ratio = originalValue / getHeaderRefreshingHeight();
        // paddingTop = (int) (-getHeaderHeight() + originalValue / Math.pow(ratio, 0.5));

        bodyIcon.setAlpha((((float) getHeaderRefreshingHeight() * progress)
            / getHeaderRefreshingHeight() - 1.5f));
        if (changed) {
          headIcon.clearAnimation();
          headIcon.animate().rotation(0).setDuration(300)
              .start();
        }
        break;
      case PULL_TO_REFRESH:
        bodyIcon.setAlpha(0);
        currentPaddingTop = (int) (-getHeaderHeight() + getHeaderRefreshingHeight() * progress);
        if (changed) {
          headIcon.clearAnimation();
          headIcon.animate().rotation(180).setDuration(300).start();
        }
        break;
      case REFRESHING:
        bodyIcon.setAlpha(0);
        currentPaddingTop = -(getHeaderHeight() - getHeaderRefreshingHeight());
        if (changed) {
          headIcon.startAnimation(circleAnimation);
        }
        break;
      case DONE:
        currentPaddingTop = -getHeaderHeight();
        headIcon.clearAnimation();
        break;
    }
    setHeaderPaddingTop(currentPaddingTop);
  }

  @Override
  public float getPullRatio(float currentPullDistance) {
    float ratio = currentPullDistance / getHeaderRefreshingHeight();
    return (float) Math.pow(ratio, 0.5);
  }

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    this.headerHeight = measuredHeight;
  }
}
