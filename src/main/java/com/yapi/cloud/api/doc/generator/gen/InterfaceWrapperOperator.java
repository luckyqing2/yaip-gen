package com.yapi.cloud.api.doc.generator.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import com.alibaba.fastjson.JSON;

/**
 * @version 8.0.0
 * @program: api-doc-generator
 * @author: "清歌"
 * @create: 2020-06-05 17:19

 */

public class InterfaceWrapperOperator {


    public static Map<String, String> FIELD_YAPI_MAPPING = new HashMap();

    static {
        FIELD_YAPI_MAPPING.put("String", "string");
        FIELD_YAPI_MAPPING.put("Integer", "integer");
        FIELD_YAPI_MAPPING.put("Long", "number");
        FIELD_YAPI_MAPPING.put("BigDecimal", "number");
        FIELD_YAPI_MAPPING.put("Byte", "number");
        FIELD_YAPI_MAPPING.put("Short", "number");
        FIELD_YAPI_MAPPING.put("Float", "number");
        FIELD_YAPI_MAPPING.put("Double", "number");
        //TODO :list
        FIELD_YAPI_MAPPING.put("List", "array");
        FIELD_YAPI_MAPPING.put("Boolean", "boolean");
        FIELD_YAPI_MAPPING.put("Map", "object");
    }


    /**
     * 封装请求bodyproperties数据
     *
     * @Param: fieldTypeMap  字段类型map key ，字段名称， value 为type 具体的（yapi的类型）类型，例如：string,integer
     * @Param: fieldCommentMap 字段备注类型  key 字段名称  value 为注释信息
     * @Param: required  是否为必须填写的值 ，
     * @return: java.util.Map
     * @Author: 清歌
     * @Date: 2020/6/5 17:17
     */
    public static void wrapperRequestOtherBodyMessage(Document interfaceDoc, Map<String, String> fieldTypeMap,
            Map<String, String> fieldCommentMap, List<String> required, Set<String> excludeField) {

        HashMap reqBodyOtherMap = new HashMap<>();

        HashMap propertiesMap = new HashMap<>();
        for (Entry<String, String> field : fieldTypeMap.entrySet()) {
            String key = field.getKey();
            if (excludeField.contains(key)) {
                continue;
            }
            String type = field.getValue();
            String comment = fieldCommentMap.get(key);
            HashMap<Object, Object> propertiesValueMap = new HashMap<>();
            propertiesValueMap.put("type", type);
            propertiesValueMap.put("default", "1");
            propertiesValueMap.put("description", comment);
            propertiesMap.put(key, propertiesValueMap);
        }
        reqBodyOtherMap.put("properties", propertiesMap);
        reqBodyOtherMap.put("type", "object");
        reqBodyOtherMap.put("required", required);
        String string = JSON.toJSONString(reqBodyOtherMap);
        System.out.println("reqBodyOtherMap string" + string);
        interfaceDoc.put("req_body_other", string);
    }


    public static String getColumnType(Class fieldType) {
        //String,Integer,Long,BigDecimal,Byte,Short,Float,Double
        String simpleName = fieldType.getSimpleName();
        String value = FIELD_YAPI_MAPPING.get(simpleName);
        if (StringUtils.isBlank(value)) {
            value = "object";
        }
        return value;
    }

    public static void wrapperInterfaceMessage(Document interfaceDoc, String path, String title, Long catId,
            Integer projectId) {

        interfaceDoc.put("edit_uid", 0);
        interfaceDoc.put("status", "undone");
        interfaceDoc.put("type", "static");
        interfaceDoc.put("req_body_is_json_schema", true);
        interfaceDoc.put("res_body_is_json_schema", true);
        interfaceDoc.put("api_opened", false);
        interfaceDoc.put("index", 0);
        interfaceDoc.put("tag", new ArrayList<>());

        interfaceDoc.put("method", "POST");
        interfaceDoc.put("catid", catId);
        interfaceDoc.put("title", title);
        interfaceDoc.put("path", path);
        interfaceDoc.put("project_id", projectId);
        interfaceDoc.put("req_params", new ArrayList<>());
        interfaceDoc.put("res_body_type", "json");
        HashMap<String, Object> map = new HashMap<>();
        map.put("path", path);
        map.put("params", new ArrayList<>());
        interfaceDoc.put("query_path", map);

        interfaceDoc.put("uid", 11);
        //10位时间戳
        interfaceDoc.put("add_time", System.currentTimeMillis() / 1000);
        interfaceDoc.put("up_time", System.currentTimeMillis() / 1000);
        interfaceDoc.put("req_query", new ArrayList<>());
        interfaceDoc.put("req_body_form", new ArrayList<>());
        interfaceDoc.put("__v", 0);
        interfaceDoc.put("desc", "");
        interfaceDoc.put("markdown", "");

        HashMap<String, Object> reqHeadersMap = new HashMap<>();
        reqHeadersMap.put("required", "1");
        reqHeadersMap.put("_id", System.currentTimeMillis());
        reqHeadersMap.put("name", "Content-Type");
        reqHeadersMap.put("value", "application/json");
        interfaceDoc.put("req_headers", Arrays.asList(reqHeadersMap));
        interfaceDoc.put("req_body_type", "json");

        HashMap resBodyMap = new HashMap<>();
        resBodyMap.put("type", "object");
        resBodyMap.put("title", "empty object");
        resBodyMap.put("properties", "");
        interfaceDoc.put("res_body", resBodyMap);
    }

}
