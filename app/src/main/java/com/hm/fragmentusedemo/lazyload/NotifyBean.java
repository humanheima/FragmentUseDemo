package com.hm.fragmentusedemo.lazyload;

/**
 * Created by 19658 on 2017/6/14.
 */

public class NotifyBean {

    /**
     * article_id : 3
     * add_time : 1498465276
     * title : 123123
     * cat_id : 1
     * url : http://cgg.265nt.com/index.php/Home/index/notices/article_id/3
     */

    private long add_time;
    private String title;

    public NotifyBean(long add_time, String title) {
        this.add_time = add_time;
        this.title = title;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
