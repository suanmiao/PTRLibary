package me.suanmiao.ptrexample.io.http.requests;

import com.android.volley.Request;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import me.suanmiao.common.io.http.CommonRequest;
import me.suanmiao.common.io.http.volley.BaseXmlActionDelivery;
import me.suanmiao.ptrexample.ui.mvc.Model.ChannelModel;

/**
 * Created by suanmiao on 15/1/19.
 */
public class ChannelRequest extends CommonRequest<ChannelModel> {

  public ChannelRequest(String url) {
    super(Request.Method.GET, url,null,null,new BaseXmlActionDelivery<ChannelModel>(ChannelModel.class) {
        @Override
        public Serializer getSerializer() {
            return new Persister();
        }
    });
  }

}
