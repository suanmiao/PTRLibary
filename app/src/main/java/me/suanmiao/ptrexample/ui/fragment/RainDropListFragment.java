package me.suanmiao.ptrexample.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import me.suanmiao.common.io.http.robospiece.api.BaseFormResult;
import me.suanmiao.common.ui.adapter.listview.BaseListAdapter;
import me.suanmiao.common.ui.fragment.AbstractBaseFragment;
import me.suanmiao.common.ui.mvc.ViewModel.BaseViewModel;
import me.suanmiao.ptrexample.R;
import me.suanmiao.ptrexample.ui.adapter.listview.ExampleListAdapter;
import me.suanmiao.ptrexample.ui.mvc.Model.ArticleModel;
import me.suanmiao.ptrexample.ui.mvc.Model.ExampleItemModel;
import me.suanmiao.ptrlistview.PTRListView;
import me.suanmiao.ptrlistview.header.RaindropHeader;


/**
 * Created by suanmiao on 14-12-2.
 */
public class RainDropListFragment extends AbstractBaseFragment {

  @InjectView(R.id.list_main_article)
  PTRListView listMainArticle;

  private ExampleListAdapter listAdapter;
  private static final int ITEM_COUNT_PER_REQUEST = 20;

  private Handler mHandler;

  private static String[] image =
      {
          "http://b.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D59%2C0%2C238%2C238%3Bq%3D90/sign=57137379d1160924c325a518ec3c5688/cefc1e178a82b901ab7ed71c728da9773812efca.jpg",
          "http://e.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D23%2C0%2C238%2C238%3Bq%3D90/sign=6a991b91ca134954611eef676e75f12a/b812c8fcc3cec3fdc8450465d788d43f879427a2.jpg",
          "http://f.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D92%2C0%2C238%2C238%3Bq%3D90/sign=90f6f2753af33a87816d0719fe67734a/d4628535e5dde711d52032b3a7efce1b9c166180.jpg",
          "http://c.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D14%2C0%2C238%2C238%3Bq%3D90/sign=52c40b92377adab422d01c40b3efd06e/54fbb2fb43166d22e597ad2e472309f79152d2ac.jpg",
          "http://c.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D71%2C0%2C238%2C238%3Bq%3D90/sign=4f44e315b3b7d0a264c9039ef3d4157b/bd315c6034a85edf6fc1718448540923dc5475cc.jpg",
          "http://a.hiphotos.baidu.com/album/h%3D238%3Bcrop%3D71%2C0%2C238%2C238%3Bq%3D90/sign=2a64688bb17eca800d053ee4a918f4af/f2deb48f8c5494ee686ac1b12cf5e0fe98257eca.jpg",
          "http://d.hiphotos.baidu.com/album/w%3D350%3Bcrop%3D0%2C30%2C350%2C252%3Bq%3D90/sign=9c670a6e06082838680dda1188a2ca73/500fd9f9d72a60599e35406b2934349b033bba9e.jpg",
          "http://f.hiphotos.baidu.com/album/h%3D252%3Bcrop%3D13%2C0%2C350%2C252%3Bq%3D90/sign=7968a5902e2eb938f36d7df7e759e647/c83d70cf3bc79f3dd5534241bba1cd11728b2914.jpg",
          "http://c.hiphotos.baidu.com/album/w%3D480%3Bcrop%3D0%2C14%2C480%2C517%3Bq%3D90/sign=c233a0da023b5bb5bed721f606e8b64b/3801213fb80e7becbee0a2902e2eb9389b506b8d.jpg",
          "http://h.hiphotos.baidu.com/album/w%3D480%3Bcrop%3D0%2C15%2C480%2C517%3Bq%3D90/sign=947a916aa18b87d65042aa1737334b4a/21a4462309f79052bf4464650cf3d7ca7bcbd515.jpg",
          "http://g.hiphotos.baidu.com/album/w%3D350%3Bcrop%3D0%2C26%2C350%2C252%3Bq%3D90/sign=cf245ef6267f9e2f70351b0d2f0b8a56/bf096b63f6246b60629c5e64eaf81a4c510fa214.jpg",
      };

  private static String[] texts = {
      "我的母亲",
      "简书干货内容推荐",
      "如果你是男生，读李娟吧",
      "致不曾写过文字的人",
      "自学方法",
      "简单几步教你不用vpn照样翻墙",
      "互联网职业必读的好书推荐",
      "爱情需要相濡以沫，唯独经不起消磨",
      "思想的独立比什么都重要-给未来孩子的第一封信",
      "性与爱的那道坡儿",
      "余秀华——穿越大半个中国去睡你，然后呢？",
      "由大学选课而感",
      "一日一APP:单读 IOS APP",
      "那个在大理隐居的女子",
  };

  @Override
  public int getLayoutId() {
    return R.layout.fragment_article_list;
  }

  @Override
  public void afterInjected(View contentView, Bundle savedInstanceState) {
    initWidget();
  }

  private void initWidget() {
    mHandler = new Handler();
    listAdapter = new ExampleListAdapter(getActivity());
    listAdapter.setParser(listParser);
    listAdapter.setUIChangeListener(mUiChangeListener);
    listMainArticle.setonRefreshListener(refreshListener);
    listMainArticle.setOnLoadListener(loadListener);
    listMainArticle.setOnScrollListener(listAdapter);
    listMainArticle.setAdapter(listAdapter);
    listMainArticle.setHeader(new RaindropHeader(getActivity()));
  }

  private BaseViewModel.UIChangeListener mUiChangeListener = new BaseViewModel.UIChangeListener() {
    @Override
    public void notifyUIChange() {
      listAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyException(SpiceException exception) {

    }

    @Override
    public void notifyStatus(BaseFormResult baseFormResult) {

    }
  };

  private BaseListAdapter.ListParser<ArticleModel> listParser =
      new BaseListAdapter.ListParser<ArticleModel>() {
        @Override
        public List<ArticleModel> parseData(List<ArticleModel> dataList) {
          return dataList;
        }
      };

  private PTRListView.OnRefreshListener refreshListener = new PTRListView.OnRefreshListener() {
    @Override
    public void onRefresh() {
      refreshData();
    }
  };

  private PTRListView.OnLoadListener loadListener = new PTRListView.OnLoadListener() {
    @Override
    public void onLastItemVisible() {
      loadData();
    }
  };

  private void refreshData() {
    listMainArticle.onRefreshStart();
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        listAdapter.setData(mockUp());
        listMainArticle.onRefreshComplete();
      }
    }, 2000);
  }

  private void loadData() {
    listMainArticle.onLoadStart();
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        listAdapter.addData(mockUp());
        listMainArticle.onLoadComplete();
      }
    }, 2000);
  }

  @Override
  protected void onPrepareLoading() {}

  @Override
  protected void onStartLoading() {
    refreshData();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  private List<ExampleItemModel> mockUp() {
    List<ExampleItemModel> itemModels = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      ExampleItemModel model = new ExampleItemModel(random(image), random(texts));
      itemModels.add(model);
    }
    return itemModels;
  }

  private String random(String[] array) {
    return array[(int) (Math.random() * array.length)];
  }

}
