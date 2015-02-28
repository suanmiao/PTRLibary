package me.suanmiao.ptrexample.io.http.api;

import com.google.gson.Gson;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Field;

import me.suanmiao.ptrexample.io.http.conveter.SGsonConverter;
import retrofit.RestAdapter;

/**
 * Created by suanmiao on 14/12/6.
 */
public class WaceRequestService extends RetrofitGsonSpiceService {

    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        if(okHttpClient==null){
            okHttpClient = new OkHttpClient();
        }
        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(APIConstants.BASE_URL)
                    .setConverter(new SGsonConverter(new Gson()))
                    .build();
            Field[] restAdapterField = this.getClass().getSuperclass().getSuperclass().getDeclaredFields();
            restAdapterField[2].setAccessible(true);
            restAdapterField[2].set(this, restAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        addRetrofitInterface(APIService.class);
    }

    public static OkHttpClient getOkHttpClient() {
        if(okHttpClient==null){
           okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    @Override
    protected String getServerUrl() {
        return APIConstants.BASE_URL;
    }

}
