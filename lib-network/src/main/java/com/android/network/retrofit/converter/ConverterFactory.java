package com.android.network.retrofit.converter;


import com.android.network.Null;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 将响应对象responseBody转成目标类型对象(也就是Call里给定的类型)
 *
 * @param <T>
 */
public class ConverterFactory<T> extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                if (body.contentLength() == 0)
                    return Null.INSTANCE;
                return delegate.convert(body);
            }
        };
    }
}
