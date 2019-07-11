package com.android.network.retrofit.converter;

import android.text.TextUtils;

import com.android.network.ApiResponse;
import com.android.network.Null;
import com.android.network.error.ErrorException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 1、处理无响应体的response
 * 2、捕获response，处理业务异常
 */
public class DataConverterFactory<T> extends Converter.Factory {
    private static final String TAG = "DataConverterFactory";

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, Retrofit retrofit) {
        // 此处代码在主线程执行
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                // 此处为异步操作
                // 处理网络层200的情况下
                if (body.contentLength() == 0) { // 转换无响应体的response
                    return Null.INSTANCE;
                } else { // 处理业务逻辑
                    // 获取泛式类型
                    Type wraperType = new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            Type[] types = new Type[1];
                            types[0] = type;
                            return types;
                        }

                        @Override
                        public Type getRawType() {
                            return ApiResponse.class;
                        }

                        @Override
                        public Type getOwnerType() {
                            return ApiResponse.class;
                        }
                    };
                    // 使用Gson的反序列化将json转化成具体的对象
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(wraperType)); // 此处使用ApiResponse类型
                    JsonReader jsonReader = gson.newJsonReader(body.charStream());
                    ApiResponse apiResponse = (ApiResponse) adapter.read(jsonReader);
                    try { // 200下，处理数据，禁止捕获异常
                        if (TextUtils.isEmpty(apiResponse.getResultCode())) {
                            throw new JsonSyntaxException("java.lang.IllegalStateException: Expected BEGIN");
                        }
                        int resultCode = Integer.valueOf(apiResponse.getResultCode());
                        if (resultCode != 200) { // 非200下，抛出自定义异常
                            throw new ErrorException(resultCode, apiResponse.getMessage());
                        }
                        if (type == Null.class || null == apiResponse.getData()) {
                            return Null.INSTANCE;
                        } else {
                            return apiResponse.getData();
                        }
                    } finally {
                        body.close();
                    }
                }
            }
        };
    }
}
