package me.suanmiao.ptrlistview.header.paddingHeader;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.suanmiao.ptrlistview.IPullToRefresh;
import me.suanmiao.ptrlistview.R;
import me.suanmiao.ptrlistview.header.IPTRHeader;


/**
 * Created by suanmiao on 14-9-15.
 */
public class RaindropHeader extends LinearLayout implements IPTRHeader {

  private ViewGroup headerContainer;
  private int headerHeight;
  public static final long RESET_TOTAL_DURATION = 300;
  private static final long ANIMATION_DURATION = 500;
  private boolean shrinking = false;
  private boolean inChangeable = false;
  private int currentPaddingTop;
  private int waterDropMaringTop;

  private int waterDropHeight;
  private int waterDropWidth;
  private int waterDropPaddingBottom;

  private Paint paint;
  private Path outlinePath;
  private Path contentPath;
  private boolean drawDebugInfo = false;

  private int contentFillColor;
  private int outlineColor;

  public RaindropHeader(Context context) {
    super(context);
    init(context);
  }

  public RaindropHeader(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public RaindropHeader(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  public void init(Context context) {
    /**
     * must add a child whose layout param is specific
     */
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        context.getResources().getDimensionPixelSize(R.dimen.rain_drop_header_total_height)));

    contentFillColor = getResources().getColor(R.color.water_drop_fill_color);
    outlineColor = getResources().getColor(R.color.water_drop_border_color);
    waterDropWidth = getResources().getDimensionPixelSize(R.dimen.water_drop_width);
    waterDropMaringTop = getResources().getDimensionPixelSize(R.dimen.water_drop_margin_top);
    waterDropHeight = getResources().getDimensionPixelSize(R.dimen.water_drop_width);
    waterDropPaddingBottom =
        getResources().getDimensionPixelSize(R.dimen.water_drop_padding_bottom);
    paint = new Paint();
    paint.setAntiAlias(true);
    outlinePath = new Path();
    contentPath = new Path();

    setWillNotDraw(false);
  }

  @Override
  public void onPull(float progress, IPullToRefresh.STATE refreshState, boolean stateChanged) {
    switch (refreshState) {
      case RELEASE_TO_REFRESH: {
        currentPaddingTop = (int) (-getHeaderHeight() + progress * getHeaderRefreshingHeight());
        int dropHeight =
            (int) (getHeaderRefreshingHeight() * progress);
        if (!shrinking && !inChangeable) {
          if (dropHeight > getResources().getDimensionPixelSize(R.dimen.water_drop_max_height)) {
            shrink();
          } else if (dropHeight >= waterDropWidth + waterDropMaringTop) {
            this.waterDropHeight = dropHeight - waterDropMaringTop;
          }
          invalidate();
        }
        break;
      }
      case PULL_TO_REFRESH: {
        currentPaddingTop = (int) (-getHeaderHeight() + progress * getHeaderRefreshingHeight());
        int dropHeight =
            (int) (getHeaderRefreshingHeight() * progress);
        if (!shrinking && !inChangeable) {
          if (dropHeight > getResources().getDimensionPixelSize(R.dimen.water_drop_max_height)) {
            shrink();
          } else if (dropHeight >= waterDropWidth + waterDropMaringTop) {
            this.waterDropHeight = dropHeight - waterDropMaringTop;
          }
          invalidate();
        }
        break;
      }
      case DONE: {
        currentPaddingTop = -getHeaderHeight();
        if (!inChangeable) {
          shrink();
        }
        break;
      }
      case REFRESHING: {
        currentPaddingTop = -(getHeaderHeight() - getHeaderRefreshingHeight());
        if (!inChangeable) {
          shrink();
        }
        break;
      }
    }
    setHeaderPaddingTop(currentPaddingTop);
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

  protected void setHeaderPaddingTop(int paddingTop) {
    headerContainer.setPadding(headerContainer.getPaddingLeft(), paddingTop,
        headerContainer.getPaddingRight(), headerContainer.getPaddingBottom());
  }

  @Override
  public void onPullCancel() {
    inChangeable = false;
    animatePaddingTop(-headerHeight);
  }

  @Override
  public void onRefreshStart() {
    animatePaddingTop(-headerHeight + getHeaderRefreshingHeight());
  }

  @Override
  public void onRefreshComplete() {
    inChangeable = false;
    animatePaddingTop(-headerHeight);
  }

  @Override
  public void onInit(ViewGroup headerContainer) {
    this.headerContainer = headerContainer;
    inChangeable = false;
  }

  @Override
  public float getPullRatio(float currentPullDistance) {
    float ratio = currentPullDistance / getHeaderRefreshingHeight();
    return (float) Math.pow(ratio, 0.5);
  }

  @Override
  public int getHeaderHeight() {
    return headerHeight;
  }

  @Override
  public void afterHeaderMeasured(int measuredHeight) {
    headerHeight = measuredHeight;
  }

  @Override
  public int getHeaderRefreshingHeight() {
    return getResources().getDimensionPixelSize(R.dimen.water_drop_refreshing_height);
  }

  @Override
  public View getHeaderContent(ViewGroup headerContainer) {
    return this;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    drawContent(canvas);
    super.onDraw(canvas);
  }

  public void shrink() {
    inChangeable = true;
    shrinking = true;
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1f).setDuration(ANIMATION_DURATION);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float t = animation.getDuration() * animation.getAnimatedFraction();
        float b = 0f;
        float c = 1f;
        float d = animation.getDuration();
        float result = 2 - calculate(t, b, c, d);
        waterDropHeight = (int) (waterDropWidth * result);
        invalidate();
      }
    });
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        shrinking = false;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    animator.start();
  }

  /**
   *
   * @param t now time,time has passed
   * @param b start value
   * @param c end value - start value
   * @param d total duration
   * @return result value
   */

  public Float calculate(float t, float b, float c, float d) {
    if (t == 0) return b;
    if ((t /= d) == 1) return b + c;
    float p = d * .3f;
    float a = c;
    float s = p / 4;
    return (a * (float) Math.pow(2, -10 * t)
        * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) + c + b);
  }

  private void drawContent(Canvas canvas) {
    ArrayList<Node> drawDataNodeList;
    paint.setColor(contentFillColor);
    outlinePath.reset();
    contentPath.reset();

    drawDataNodeList = new ArrayList<Node>();

    int paddingX = (getWidth() - waterDropWidth) / 2;
    int dropRadius = waterDropWidth / 2;
    // int paddingY = waterDropPaddingBottom;
    drawDataNodeList.add(new Node(paddingX + dropRadius, getHeight() - waterDropPaddingBottom
        - waterDropHeight));
    drawDataNodeList.add(new Node(paddingX + waterDropWidth, getHeight() - waterDropPaddingBottom
        - waterDropHeight + dropRadius));
    drawDataNodeList.add(new Node(paddingX + dropRadius, getHeight() - waterDropPaddingBottom));
    drawDataNodeList.add(new Node(paddingX, getHeight() - waterDropPaddingBottom -
        waterDropHeight
        + dropRadius));

    boolean isClosePath = false;
    ArrayList<Node> middlePointList = getMidPointList(drawDataNodeList);
    if (middlePointList.size() == drawDataNodeList.size()) {
      isClosePath = true;
    }
    ArrayList<Node> movedMidPointList = new ArrayList<Node>();
    // midpoint more than one ,use Bazier curve
    if (middlePointList.size() > 1) {
      Node lastMidNode = null;
      Node lastDataNode = null;
      for (int i = 0; i < middlePointList.size(); i++) {
        Node currentMidNode = middlePointList.get(i);
        Node currentDataNode = drawDataNodeList.get(i);
        Node nextDataNode =
            drawDataNodeList.get((i + 1 + drawDataNodeList.size()) % drawDataNodeList.size());
        if (lastMidNode != null) {
          float startMidPointLineLength =
              (float) Math.sqrt((currentDataNode.x - lastDataNode.x)
                  * (currentDataNode.x - lastDataNode.x)
                  + (currentDataNode.y - lastDataNode.y) * (currentDataNode.y - lastDataNode.y));

          float endMidPointLineLength =
              (float) Math.sqrt((nextDataNode.x - currentDataNode.x)
                  * (nextDataNode.x - currentDataNode.x)
                  + (nextDataNode.y - currentDataNode.y) * (nextDataNode.y - currentDataNode.y));

          Node distance =
              getLineMoveDistance(startMidPointLineLength, endMidPointLineLength, lastMidNode,
                  currentMidNode, currentDataNode);

          Node movedLastMidNode =
              new Node((lastMidNode.x + distance.x), (lastMidNode.y + distance.y));
          Node movedCurrentMidNode =
              new Node((currentMidNode.x + distance.x), (currentMidNode.y + distance.y));
          movedMidPointList.add(movedLastMidNode);
          movedMidPointList.add(movedCurrentMidNode);
        }
        lastMidNode = currentMidNode;
        lastDataNode = currentDataNode;
      }

      // close path thus add first midLine
      if (isClosePath) {
        lastMidNode = middlePointList.get(middlePointList.size() - 1);
        Node currentMidNode = middlePointList.get(0);

        lastDataNode = drawDataNodeList.get(drawDataNodeList.size() - 1);
        Node currentDataNode = drawDataNodeList.get(0);
        Node nextDataNode = drawDataNodeList.get(1);

        float startMidPointLineLength =
            (float) Math.sqrt((currentDataNode.x - lastDataNode.x)
                * (currentDataNode.x - lastDataNode.x)
                + (currentDataNode.y - lastDataNode.y) * (currentDataNode.y - lastDataNode.y));

        float endMidPointLineLength =
            (float) Math.sqrt((nextDataNode.x - currentDataNode.x)
                * (nextDataNode.x - currentDataNode.x)
                + (nextDataNode.y - currentDataNode.y) * (nextDataNode.y - currentDataNode.y));

        Node distance =
            getLineMoveDistance(startMidPointLineLength, endMidPointLineLength, lastMidNode,
                currentMidNode, currentDataNode);

        Node movedLastMidNode =
            new Node((lastMidNode.x + distance.x), (lastMidNode.y + distance.y));
        Node movedCurrentMidNode =
            new Node((currentMidNode.x + distance.x), (currentMidNode.y + distance.y));
        movedMidPointList.add(movedLastMidNode);
        movedMidPointList.add(movedCurrentMidNode);
      }
    }
    Node firstNode = drawDataNodeList.get(0);
    outlinePath.moveTo(firstNode.x, firstNode.y);
    contentPath.moveTo(firstNode.x, firstNode.y);

    if (isClosePath) {
      for (int i = 0; i < drawDataNodeList.size() + 1; i++) {
        int mid1Index = ((i - 1) * 2 - 1 + movedMidPointList.size())
            % movedMidPointList.size();
        int mid2Index = ((i - 1) * 2 + movedMidPointList.size())
            % movedMidPointList.size();
        Node currentMidNode1 =
            movedMidPointList.get(mid1Index);
        Node currentMidNode2 =
            movedMidPointList.get(mid2Index);
        Node targetNode =
            drawDataNodeList.get(i % drawDataNodeList.size());
        if (i == 0) {
          outlinePath.moveTo(targetNode.x, targetNode.y);
          contentPath.moveTo(targetNode.x, targetNode.y);
        } else {
          outlinePath.cubicTo(currentMidNode1.x, currentMidNode1.y, currentMidNode2.x,
              currentMidNode2.y, targetNode.x, targetNode.y);
          contentPath.cubicTo(currentMidNode1.x, currentMidNode1.y, currentMidNode2.x,
              currentMidNode2.y, targetNode.x, targetNode.y);
        }
      }
    } else {
      for (int i = 0; i < drawDataNodeList.size() - 1; i++) {
        Node targetNode = drawDataNodeList.get((i + 1) % drawDataNodeList.size());
        Node midNode1 = movedMidPointList.get((i * 2 - 1) % movedMidPointList.size());
        Node midNode2 = movedMidPointList.get((i * 2) % movedMidPointList.size());
        outlinePath.cubicTo(midNode1.x, midNode1.y, midNode2.x, midNode2.y, targetNode.x,
            targetNode.y);
        contentPath.cubicTo(midNode1.x, midNode1.y, midNode2.x, midNode2.y, targetNode.x,
            targetNode.y);
      }
    }
    contentPath.close();
    outlinePath.close();

    // draw content
    paint.setColor(contentFillColor);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawPath(contentPath, paint);

    if (drawDebugInfo) {
      // draw debug
      int radius = 5;
      paint.setColor(Color.BLUE);
      for (int i = 0; i < drawDataNodeList.size(); i++) {
        Node node = drawDataNodeList.get(i);
        canvas.drawRect(node.x - radius, node.y - radius, node.x + radius, node.y + radius, paint);
        canvas.drawText(i + "", node.x + radius + 10, node.y, paint);
      }

      paint.setColor(Color.RED);
      for (int i = 0; i < middlePointList.size(); i++) {
        Node node = middlePointList.get(i);
        canvas.drawRect(node.x - radius, node.y - radius, node.x + radius, node.y + radius, paint);
        canvas.drawText(i + "", node.x + radius + 10, node.y, paint);
      }
      paint.setColor(Color.GREEN);
      for (int i = 0; i < movedMidPointList.size(); i++) {
        Node node = movedMidPointList.get(i);
        canvas.drawRect(node.x - radius, node.y - radius, node.x + radius, node.y + radius, paint);
        canvas.drawText(i + "", node.x + radius + 10, node.y, paint);
      }
    }

    // draw outline
    paint.setColor(outlineColor);
    paint.setStyle(Paint.Style.FILL);
    // paint.setStrokeWidth(getResources().getDimensionPixelSize(
    // R.dimen.water_drop_border_stroke_width));
    canvas.drawPath(outlinePath, paint);
  }

  public ArrayList<Node> getMidPointList(ArrayList<Node> dataList) {
    ArrayList<Node> resultList = new ArrayList<Node>();
    if (dataList != null && dataList.size() > 0) {
      if (dataList.size() > 1) {
        float lastX = -1;
        float lastY = -1;
        for (int i = 0; i < dataList.size(); i++) {
          Node currentNode = dataList.get(i);
          // last point exist
          if (lastX != -1 || lastY != -1) {
            resultList.add(new Node((lastX + currentNode.x) / 2, (lastY + currentNode.y) / 2));
          }
          lastX = currentNode.x;
          lastY = currentNode.y;
        }
        Node startNode = dataList.get(0);
        // last point equals first point thus path close
        resultList.add(new Node((startNode.x + lastX) / 2, (startNode.y + lastY) / 2));
      } else {
        resultList.add(dataList.get(0));
      }
    }
    return resultList;
  }

  private Node getLineMoveDistance(float startPointLineLength, float endPointLineLength,
      Node start, Node end, Node target) {
    float distanceX = end.x - start.x;
    float distanceY = end.y - start.y;
    float moveDistanceX =
        distanceX * (startPointLineLength / (startPointLineLength + endPointLineLength));
    float moveDistanceY =
        distanceY * (startPointLineLength / (startPointLineLength + endPointLineLength));
    float resultX = start.x + moveDistanceX;
    float resultY = start.y + moveDistanceY;
    return new Node(target.x - resultX, target.y - resultY);
  }

  public class Node {
    public final float x;
    public final float y;

    public Node(float x, float y) {
      this.x = x;
      this.y = y;
    }
  }

}
