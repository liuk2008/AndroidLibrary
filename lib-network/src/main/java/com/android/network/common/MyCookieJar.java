package com.android.network.common;

import java.util.List;

/**
 * Created by LiuK on 2019/7/8
 */
public interface MyCookieJar {

    /**
     * 向http请求中添加cookie
     *
     * @param url 请求url
     * @return cookie集合
     */
    List<MyCookie> cookieForRequest(String url);

    /**
     * 解析http响应中cookie值
     *
     * @param url       请求url
     * @param myCookies cookie集合
     */
    void cookieFromResponse(String url, List<MyCookie> myCookies);

}
