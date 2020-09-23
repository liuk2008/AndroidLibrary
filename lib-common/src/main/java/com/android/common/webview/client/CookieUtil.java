package com.android.common.webview.client;


import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.CookieManager;


/**
 * 对系统CookieManager进行Cookie的写入操作
 * 1、设置cookie时，会检测WebView加载的url值、setCookie中的url值、cookie的domain值，三者必须一致
 * 2、只有cookie的domain和path与请求的url匹配才会发送这个cookie
 * <p>
 * 关于cookie中的domain域名设置
 * 1、domain为通配域名时（.baidu.com）可以跨域访问，domain为全域名时（www.baidu.com）只能单独访问
 * 2、显示设置domain时，系统自动在domain值前面加点（.），例如domain=baidu.com，事实上值是.baidu.com，
 * 3、不显示设置domain时，使用默认值就不会加点，那么cookie只对当前域名有效
 * <p>
 * cookie中的path其实就是url发布的路径，比如：
 * https://xxx/foo/bar/index，path=/foo/bar
 * https://xxx/foo/index，path=/foo
 * https://xxx/index，path=/
 * https://xxx/foo/bar/index，设置path=/bar，则cookie无法发送
 * <p>
 * 正确使用cookie中的domain：
 * https://www.cnblogs.com/wenjia-hao/p/7365569.html
 */
public class CookieUtil {

    private static final String TAG = "CookieUtil";

    /**
     * @param url   WebView加载的url
     * @param key   cookie name
     * @param value cookie value
     */
    public static void setCookie(@Nullable String url, @Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) return;
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
            url = Uri.parse(url).getHost();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookie = buildCookie(url, key, value);
        cookieManager.setCookie(url, cookie); // url在里边起到作用，就是检测domain域名。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush(); // 立即同步cookie的操作
        }
    }

    /**
     * 清除cookie
     */
    public static void clearCookie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
    }

    /**
     * 通过过期Cookie覆盖之前的Value，以移除特定Cookie的Value
     */
    public static void removeExpiredCookie(String domain, String key) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(domain, buildExpireCookie(domain, key));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            cookieManager.removeExpiredCookie();
        }
    }

    private static String buildCookie(String domain, String name, String value) {
        return name + "=" + value + "; domain=" + domain + "; path=/";
    }

    private static String buildExpireCookie(String domain, String name) {
        return name + "=; domain=" + domain + "; path=/ ;Expires=1 Jan 1970 00:00:00 GMT";
    }

}
