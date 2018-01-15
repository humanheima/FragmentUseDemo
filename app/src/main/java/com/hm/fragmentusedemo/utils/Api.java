package com.hm.fragmentusedemo.utils;

import com.hm.fragmentusedemo.lazyload.NotifyBean;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by dumingwei on 2018/1/15 0015.
 */

public interface Api {

    //通知列表
    @FormUrlEncoded
    @POST("Apis/Member/notice")
    Observable<MsgResult<List<NotifyBean>>> getNotifyList(@Field("cat_id") int i, @Field("p") int page,
                                                          @Field("uniquekey") String uniquekey);
}
