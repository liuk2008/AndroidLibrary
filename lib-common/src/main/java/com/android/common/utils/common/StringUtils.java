package com.android.common.utils.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/4/4.
 */
public class StringUtils {

    /**
     * 将字节输入流转换为字符串
     */
    public static String getString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] bys = new byte[1024];
        while ((len = in.read(bys)) != -1) {
            out.write(bys, 0, len);
        }
        in.close();
        out.close();
        return out.toString();
    }

}
