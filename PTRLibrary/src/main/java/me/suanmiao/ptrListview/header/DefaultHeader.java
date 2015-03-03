package me.suanmiao.ptrlistview.header;

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
public class DefaultHeader implements IPTRHeader {

  private Context mContext;

  public int paddingTop = 0;
  private View headerLayout;
  private TextView ptrHeaderTipTextView;
  private ImageView ptrHeaderArrowImageView;
  private ImageView ptrHeaderCircleImageView;
  private RotateAnimation circleAnimation;
  private static final long ARROW_ANIMATION_DURATION = 300;
  private int headerHeight;

  public DefaultHeader(Context context) {
    mContext = context;
    initLayout();
  }

  private void initLayout() {
    LayoutInflater inflater =
        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    headerLayout = inflater.inflate(
        R.layout.ptr_header_layout, null);
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

  @Override
  public View getHeaderLayout(ViewGroup container) {
    return headerLayout;
  }

  @Override
  public int onPull(float progress, IPullToRefresh.REFRESH_STATE refreshState, boolean stateChanged) {
    switch (refreshState) {
      case RELEASE_TO_REFRESH:
        paddingTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
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
        paddingTop = (int) (-headerHeight + getHeaderRefreshingHeight() * progress);
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
        paddingTop = -(headerHeight - getHeaderRefreshingHeight());
        ptrHeaderCircleImageView.setVisibility(View.VISIBLE);
        ptrHeaderCircleImageView.clearAnimation();
        ptrHeaderCircleImageView.startAnimation(circleAnimation);
        ptrHeaderArrowImageView.clearAnimation();
        ptrHeaderArrowImageView.setVisibility(View.GONE);
        ptrHeaderTipTextView.setText(mContext.getResources().getString(R.string.refreshing));
        break;
      case DONE:
        paddingTop = -headerHeight;
        ptrHeaderCircleImageView.setVisibility(View.GONE);
        ptrHeaderArrowImageView.clearAnimation();
        ptrHeaderArrowImageView.setImageResource(R.drawable.ptr_arrow);
        ptrHeaderTipTextView.setText(mContext.getResources().getString(R.string.pull_to_refresh));
        break;
    }
    return paddingTop;
  }

  @Override
  public void onPullCancel() {}

  @Override
  public void onRefreshStart() {}

  @Override
  public void onInit() {}

  @Override
  public float getPullRatio(float currentPullDistance) {
    return PTRListView.DEFAULT_PULL_RATIO;
  }

  @Override
  public int getHeaderHeight() {
    return headerHeight;
  }

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    this.headerHeight = measuredHeight;
  }

  @Override
  public int getHeaderRefreshingHeight() {
    return mContext.getResources().getDimensionPixelSize(R.dimen.default_header_refreshing_height);
  }

  @Override
  public int getHeaderCurrentPaddingTop() {
    return paddingTop;
  }


}
