package com.veni.tools.util;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * JSON解析二次封装
 */
public class JsonUtils {

    // 采取单例模式
    private static Gson gson =null;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }
    private JsonUtils() {
    }

    /**
     * @param src :将要被转化的对象
     * @return :转化后的JSON串
     * @MethodName : toJson
     * @Description : 将对象转为JSON串，此方法能够满足大部分需求
     */
    public static String toJson(Object src) {
        if (DataUtils.isEmpty(src)) {
            return gson.toJson(JsonNull.INSTANCE);
        }
        try {
            return gson.toJson(src);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param json
     * @param clas
     * @return
     * @MethodName : parseObject
     * @Description : 用来将JSON串转为对象，但此方法不可用来转带泛型的集合
     */
    public static <T> T parseObject(String json, Class<T> clas) {
        if (DataUtils.isNullString(json)) {
            return null;
        }
        if (json.equals("{}")) {
            return null;
        }
        return gson.fromJson(json, (Type) clas);
    }

    /**
     * @param json
     * @param clas
     * @return
     * @MethodName : parseObject
     * @Description : 用来将JSON串转为对象，此方法可用来转带泛型的集合
     */
    public static <T> List<T> parseArray(String json, Class<T> clas) {
        if (DataUtils.isNullString(json)) {
            return null;
        }
        ArrayList<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            mList.add(gson.fromJson(elem, clas));
        }
        return mList;
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> parseListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> parseMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

}
