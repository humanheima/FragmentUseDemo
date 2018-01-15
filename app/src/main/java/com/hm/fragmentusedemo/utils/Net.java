package com.hm.fragmentusedemo.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by 19658 on 2017/6/2.
 */

public class Net {

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static Api api;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder mBuilder;

    static {
        mBuilder = new OkHttpClient.Builder();
    }

    public static Api getInstance() {
        if (api == null) {
            okHttpClient = mBuilder
                    .retryOnConnectionFailure(true)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .baseUrl("http://shengongguan.xun-ao.com/index.php/")
                    .build();

            api = retrofit.create(Api.class);

        }
        return api;
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> Observable<T> flatResponse(final MsgResult<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.getCode() == 1) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.getResult());
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        try {
                            subscriber.onError(new APIException(response.getCode(), response.getMsg()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * 自定义异常，当接口返回的{link Response#code}不为{link Constant#OK}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {
        public int code;
        public String message;

        public APIException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

}