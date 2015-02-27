package me.suanmiao.ptrexample.component;

import me.suanmiao.common.component.BaseApplication;
import me.suanmiao.common.io.http.RequestManager;
import me.suanmiao.ptrexample.io.http.api.WaceRequestService;

/**
 * Created by suanmiao on 14-10-31.
 */
public class SApplication extends BaseApplication {

  @Override
  protected RequestManager initRequestManager() {
    return new RequestManager(this, WaceRequestService.class, WaceRequestService.getOkHttpClient());
  }
}
