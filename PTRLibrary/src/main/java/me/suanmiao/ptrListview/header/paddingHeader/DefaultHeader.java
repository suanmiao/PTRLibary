package me.suanmiao.ptrlistview.header.paddingHeader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import me.suanmiao.ptrlistview.IPullToRefresh;
import me.suanmiao.ptrlistview.PTRListView;
import me.suanmiao.ptrlistview.R;

/**
 * Created by suanmiao on 15/2/27.
 */
public class DefaultHeader extends AbstractPaddingTopHeader {

  private View headerLayout;
  private TextView ptrHeaderTipTextView;
  private ImageView ptrHeaderArrowImageView;
  private ImageView ptrHeaderCircleImageView;
  private RotateAnimation circleAnimation;
  private static final long ARROW_ANIMATION_DURATION = 300;

  public DefaultHeader(Context context) {
    super(context);
  }

  @Override
  public View getHeaderContent(ViewGroup headerContainer) {
    if (headerLayout == null) {
      LayoutInflater inflater =
          (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      // remember to inflate with container,or the result view will not have a layout param
      headerLayout = inflater.inflate(
          R.layout.ptr_normal_header_layout, headerContainer, false);
      ptrHeaderTipTextView = (TextView) headerLayout
          .findViewById(R.id.ptr_header_text_tip);

      ptrHeaderArrowImageView = (ImageView) headerLayout
          .findViewById(R.id.ptr_header_arrow);
      ptrHeaderCircleImageView = (ImageView) headerLayout
          .findViewById(R.id.ptr_header_circle);

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
  public void onPull(float progress, IPullToRefresh.STATE refreshState, boolean stateChanged) {
    switch (refreshState) {
      case RELEASE_TO_REFRESH:
        currentPaddingTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
        if (stateChanged) {
          ptrHeaderArrowImageView.setVisibility(View.VISIBLE);
          ptrHeaderCircleImageView.clearAnimation();
          ptrHeaderCircleImageView.setVisibility(View.GONE);
          ptrHeaderTipTextView.setVisibility(View.VISIBLE);
          ptrHeaderArrowImageView.animate().rotation(180).setDuration(ARROW_ANIMATION_DURATION)
              .start();
          ptrHeaderTipTextView
              .setText(mContext.getResources().getString(R.string.release_to_refresh));
        }
        break;
      case PULL_TO_REFRESH:
        currentPaddingTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
        if (stateChanged) {
          ptrHeaderCircleImageView.clearAnimation();
          ptrHeaderCircleImageView.setVisibility(View.GONE);
          ptrHeaderTipTextView.setVisibility(View.VISIBLE);
          ptrHeaderArrowImageView.clearAnimation();
          ptrHeaderArrowImageView.setVisibility(View.VISIBLE);
          // if the state comes from "release to refresh",
          // there should be a back animation for arrow
          ptrHeaderArrowImageView.animate().rotation(0).setDuration(ARROW_ANIMATION_DURATION)
              .start();
          ptrHeaderTipTextView.setText(mContext.getResources().getString(R.string.pull_to_refresh));
        }
        break;
      case REFRESHING:
        currentPaddingTop = -(headerHeight - getHeaderRefreshingHeight());
        ptrHeaderCircleImageView.setVisibility(View.VISIBLE);
        ptrHeaderCircleImageView.clearAnimation();
        ptrHeaderCircleImageView.startAnimation(circleAnimation);
        ptrHeaderArrowImageView.clearAnimation();
        ptrHeaderArrowImageView.setVisibility(View.GONE);
        ptrHeaderTipTextView.setText(mContext.getResources().getString(R.string.refreshing));
        break;
      case DONE:
        currentPaddingTop = -headerHeight;
        ptrHeaderCircleImageView.setVisibility(View.GONE);
        ptrHeaderArrowImageView.clearAnimation();
        ptrHeaderArrowImageView.setImageResource(R.drawable.ptr_arrow);
        ptrHeaderTipTextView.setText(mContext.getResources().getString(R.string.pull_to_refresh));
        break;
    }
    setHeaderPaddingTop(currentPaddingTop);
  }

  @Override
  public float getPullRatio(float currentPullDistance) {
    return PTRListView.DEFAULT_PULL_RATIO;
  }

  @Override
  public int getHeaderRefreshingHeight() {
    return mContext.getResources().getDimensionPixelSize(R.dimen.default_header_refreshing_height);
  }
}
