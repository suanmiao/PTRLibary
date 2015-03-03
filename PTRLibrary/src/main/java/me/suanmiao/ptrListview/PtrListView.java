package me.suanmiao.ptrlistview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import me.suanmiao.ptrlistview.footer.DefaultFooter;
import me.suanmiao.ptrlistview.footer.IPTRFooter;
import me.suanmiao.ptrlistview.header.DefaultHeader;
import me.suanmiao.ptrlistview.header.IPTRHeader;


/**
 * Created by lhk on 2/6/14.
 */
public class PTRListView extends ListView implements
    AbsListView.OnScrollListener, IPullToRefresh {

  public static final float DEFAULT_PULL_RATIO = 2f;
  public static final long RESET_TOTAL_DURATION = 300;
  private IPTRHeader mHeader;
  private RelativeLayout headerContainer;
  private int currentPaddingTop;
  private IPTRFooter mFooter;
  private int headerHeight;

  /**
   * normal mode
   */
  private int startY;
  private boolean lastItemVisible = false;

  private boolean isRecorded;

  /**
   * continuous mode
   */
  private float lastY;
  private float currentPullingY;

  private OnRefreshListener refreshListener;
  private OnLoadListener onLoadListener;
  private PullProgressListener progressListener;
  private OnScrollListener mScrollListener;

  private boolean catchMotionEvent;
  private boolean isLoading = false;
  private REFRESH_STATE refreshState;
  private REFRESH_STATE lastRefreshState;

  private boolean refreshEnable = true;
  private boolean loadEnable = true;
  private boolean customisePullRatioEnable = false;
  private boolean continuousPulling = false;

  private static final int VISIBLE_SLOP = 30;

  public PTRListView(Context context) {
    super(context);
    init();
  }

  public PTRListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PTRListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    super.setOnScrollListener(this);
    // init state
    refreshState = REFRESH_STATE.DONE;
    // if catch the event
    catchMotionEvent = false;

    setHeader(new DefaultHeader(getContext()));
    setFooter(new DefaultFooter(getContext()));
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (continuousPulling) {
      return continuousTouch(ev);
    } else {
      return normalTouch(ev);
    }
  }

  private boolean continuousTouch(MotionEvent ev) {
    if (catchMotionEvent && refreshEnable) {
      float currentY = ev.getY();
      if (ev.getPointerCount() >= 2 && ev.getY(1) < currentY) {
        currentY = ev.getY(1);
      }
      switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
          if (!isRecorded) {
            isRecorded = true;
          }
          lastY = currentY;// position of event start
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          if (refreshState != REFRESH_STATE.REFRESHING && refreshState != REFRESH_STATE.LOADING) {
            if (refreshState == REFRESH_STATE.PULL_TO_REFRESH) {
              refreshState = REFRESH_STATE.DONE;
              headerPullCancel();
            }
            if (refreshState == REFRESH_STATE.RELEASE_TO_REFRESH) {
              refreshState = REFRESH_STATE.REFRESHING;
              headerRefreshStart();
              if (refreshListener != null) {
                refreshListener.onRefresh();
              }
            }
          }
          isRecorded = false;
          currentPullingY = 0;
          break;
        case MotionEvent.ACTION_MOVE:
          if (!isRecorded) {
            isRecorded = true;
            lastY = currentY;
          }
          float deltaY = currentY - lastY;
          currentPullingY += deltaY;
          switch (refreshState) {
            case RELEASE_TO_REFRESH:
              // ensure the section is always the first one
              setSelection(0);
              if (currentPullingY > 0) {
                if (currentPullingY / getPullRatio(currentPullingY) < mHeader
                    .getHeaderRefreshingHeight()) {
                  refreshState = REFRESH_STATE.PULL_TO_REFRESH;
                }
              } else {
                refreshState = REFRESH_STATE.DONE;
              }
              setPullProgress(currentPullingY / getPullRatio(currentPullingY)
                  / (float) mHeader.getHeaderRefreshingHeight());
              break;
            case PULL_TO_REFRESH:
              setSelection(0);
              if (currentPullingY / getPullRatio(currentPullingY) >= mHeader
                  .getHeaderRefreshingHeight()) {
                // change state to rtr
                refreshState = REFRESH_STATE.RELEASE_TO_REFRESH;
              } else if (currentPullingY <= 0) {
                refreshState = REFRESH_STATE.DONE;
              }
              setPullProgress(currentPullingY / getPullRatio(currentPullingY)
                  / (float) mHeader.getHeaderRefreshingHeight());
              break;
            case DONE:
              if (currentPullingY > 0) {
                refreshState = REFRESH_STATE.PULL_TO_REFRESH;
                setPullProgress((currentPullingY) / getPullRatio(currentPullingY)
                    / (float) mHeader.getHeaderRefreshingHeight());
              }
              break;
          }
          break;

        default:
          break;
      }
      lastY = currentY;
    }
    try {
      return super.onTouchEvent(ev);
    } catch (Exception e) {
      return true;
    }
  }

  private boolean normalTouch(MotionEvent ev) {
    if (catchMotionEvent && refreshEnable) {
      switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
          if (!isRecorded) {
            isRecorded = true;
          }
          // find the issue ,should set start y out of record
          // else some event will not be recorded
          startY = (int) ev.getY();// position of event start
          break;
        case MotionEvent.ACTION_UP:
          if (refreshState != REFRESH_STATE.REFRESHING && refreshState != REFRESH_STATE.LOADING) {
            if (refreshState == REFRESH_STATE.PULL_TO_REFRESH) {
              refreshState = REFRESH_STATE.DONE;
              headerPullCancel();
            }
            if (refreshState == REFRESH_STATE.RELEASE_TO_REFRESH) {
              refreshState = REFRESH_STATE.REFRESHING;
              headerRefreshStart();
              if (refreshListener != null) {
                refreshListener.onRefresh();
              }
            }
          }
          isRecorded = false;
          break;
        case MotionEvent.ACTION_MOVE:
          int tempY = (int) ev.getY();
          if (!isRecorded) {
            isRecorded = true;
            startY = tempY;
          }
          switch (refreshState) {
            case RELEASE_TO_REFRESH:
              // ensure the section is always the first one
              setSelection(0);
              if ((tempY - startY) > 0) {
                if ((tempY - startY) / getPullRatio(tempY - startY) < mHeader
                    .getHeaderRefreshingHeight()) {
                  refreshState = REFRESH_STATE.PULL_TO_REFRESH;
                }
              } else {
                refreshState = REFRESH_STATE.DONE;
              }
              setPullProgress((tempY - startY) / getPullRatio(tempY - startY)
                  / (float) mHeader.getHeaderRefreshingHeight());
              break;
            case PULL_TO_REFRESH:
              setSelection(0);
              if ((tempY - startY) / getPullRatio(tempY - startY) >= mHeader
                  .getHeaderRefreshingHeight()) {
                // change state to rtr
                refreshState = REFRESH_STATE.RELEASE_TO_REFRESH;
              } else if (tempY - startY <= 0) {
                refreshState = REFRESH_STATE.DONE;
              }
              setPullProgress((tempY - startY) / getPullRatio(tempY - startY)
                  / (float) mHeader.getHeaderRefreshingHeight());
              break;
            case DONE:
              if (tempY > startY) {
                refreshState = REFRESH_STATE.PULL_TO_REFRESH;
                setPullProgress((tempY - startY) / getPullRatio(tempY - startY)
                    / (float) mHeader.getHeaderRefreshingHeight());
              }
              break;
          }
          break;

        default:
          break;
      }
    }
    try {
      return super.onTouchEvent(ev);
    } catch (Exception e) {
      return true;
    }
  }

  private void headerPullCancel() {
    mHeader.onPullCancel();
    animatePaddingTop(-headerHeight);
  }

  private void headerRefreshStart() {
    mHeader.onRefreshStart();
    animatePaddingTop(-headerHeight + mHeader.getHeaderRefreshingHeight());
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    try {
      super.dispatchDraw(canvas);
    } catch (IndexOutOfBoundsException e) {
      // samsung error
    }
  }

  public void setonRefreshListener(OnRefreshListener refreshListener) {
    this.refreshListener = refreshListener;
    catchMotionEvent = true;
  }

  @Override
  public void setOnScrollListener(OnScrollListener onScrollListener) {
    this.mScrollListener = onScrollListener;
  }

  public void setOnLoadListener(OnLoadListener onLoadListener) {
    this.onLoadListener = onLoadListener;
  }

  public void setProgressListener(PullProgressListener progressListener) {
    this.progressListener = progressListener;
  }

  public void setRefreshEnable(boolean refreshEnable) {
    this.refreshEnable = refreshEnable;
  }

  public void setLoadEnable(boolean loadEnable) {
    this.loadEnable = loadEnable;
  }

  @Override
  public REFRESH_STATE getRefreshState() {
    return refreshState;
  }

  @Override
  public boolean isRefreshEnable() {
    return refreshEnable;
  }

  @Override
  public void onRefreshStart() {
    refreshState = REFRESH_STATE.REFRESHING;
    setPullProgress(0);
  }

  @Override
  public void onRefreshComplete() {
    refreshState = REFRESH_STATE.DONE;
    mHeader.onPullCancel();
    animatePaddingTop(-mHeader.getHeaderHeight());
  }

  @Override
  public boolean isLoadEnable() {
    return loadEnable;
  }

  @Override
  public void onLoadStart() {
    isLoading = true;
    mFooter.onLoadStart();
  }

  @Override
  public void onLoadComplete() {
    isLoading = false;
    mFooter.onLoadComplete();
  }

  @Override
  public boolean isLoading() {
    return isLoading;
  }

  @Override
  public void setHeader(IPTRHeader header) {
    this.mHeader = header;
    headerContainer = new RelativeLayout(getContext());
    headerContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    addHeaderView(headerContainer);

    View headerContent = header.getHeaderLayout(headerContainer);
    ViewGroup.LayoutParams contentParams = headerContent.getLayoutParams();
    if (contentParams != null) {
      contentParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }
    headerContainer.addView(headerContent);

    measureHeader(headerContainer);
    headerHeight = headerContainer.getMeasuredHeight();
    header.afterHeaderMeasured(headerHeight);
    mHeader.onInit();
    setHeaderPaddingTop(-headerHeight);
  }

  @Override
  public void setFooter(IPTRFooter footer) {
    if (this.mFooter != null) {
      removeFooterView(this.mFooter.getFooterLayout());
    }
    this.mFooter = footer;
    addFooterView(footer.getFooterLayout(), null, false);
  }

  @Override
  public void setContinuousPulling(boolean continous) {
    this.continuousPulling = continous;
  }

  public float getPullRatio(float currentPullDistance) {
    return customisePullRatioEnable ? (mHeader == null ? DEFAULT_PULL_RATIO : mHeader
        .getPullRatio(currentPullDistance)) : DEFAULT_PULL_RATIO;
  }

  @Override
  public void setCustomisePullRatioEnable(boolean enable) {
    customisePullRatioEnable = enable;
  }

  public void setAdapter(ListAdapter adapter) {
    super.setAdapter(adapter);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE && lastItemVisible && onLoadListener != null
        && itemTakeFullPage() && loadEnable) {
      onLoadListener.onLastItemVisible();
    }

    if (mScrollListener != null) {
      mScrollListener.onScrollStateChanged(view, scrollState);
    }
  }

  /**
   * to judge whether items take full page
   * if not ,even if last item is visible ,we should not call listener
   *
   * @return whether items take up full page
   */
  private boolean itemTakeFullPage() {
    if (getChildCount() > 0) {
      int totalHeight = 0;
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        Rect childRect = new Rect();
        child.getDrawingRect(childRect);
        totalHeight += childRect.height();
      }
      Rect visibleRect = new Rect();
      getGlobalVisibleRect(visibleRect);
      return (visibleRect.bottom - visibleRect.top) - totalHeight < VISIBLE_SLOP;
    }
    return false;
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem,
      int visibleItemCount, int totalItemCount) {
    // when list is empty
    catchMotionEvent = firstVisibleItem == 0;

    lastItemVisible = firstVisibleItem + visibleItemCount >= totalItemCount;
    if (mScrollListener != null) {
      mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
          totalItemCount);
    }
  }

  public void setPullProgress(float progress) {
    boolean stateChanged = lastRefreshState != refreshState;
    if (progressListener != null) {
      progressListener.onPull(progress, refreshState, stateChanged);
    }
    if (mHeader != null) {
      currentPaddingTop = mHeader.onPull(progress, refreshState, stateChanged);
      setHeaderPaddingTop(currentPaddingTop);
    }
    lastRefreshState = refreshState;
  }

  private void setHeaderPaddingTop(int paddingTop) {
    headerContainer.setPadding(headerContainer.getPaddingLeft(), paddingTop,
        headerContainer.getPaddingRight(), headerContainer.getPaddingBottom());
  }

  private void measureHeader(View child) {
    try {
      ViewGroup.LayoutParams p = child.getLayoutParams();
      if (p == null) {
        p = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
            AbsListView.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(p);
      }

      int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
      int lpHeight = p.height;
      int childHeightSpec;
      if (lpHeight > 0) {
        childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
            View.MeasureSpec.EXACTLY);
      } else {
        childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
            View.MeasureSpec.UNSPECIFIED);
      }
      child.measure(childWidthSpec, childHeightSpec);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void animatePaddingTop(int to) {
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

  public interface OnRefreshListener {
    public void onRefresh();
  }

  public interface OnLoadListener {
    public void onLastItemVisible();
  }

  public interface PullProgressListener {
    public int onPull(float progress, REFRESH_STATE state, boolean stateChanged);
  }
}
