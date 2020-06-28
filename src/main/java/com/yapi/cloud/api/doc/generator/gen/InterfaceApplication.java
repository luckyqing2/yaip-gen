package com.yapi.cloud.api.doc.generator.gen;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSON;

/**
 * @version 8.0.0
 * @program: api-doc-generator
 * @author: "清歌"
 * @create: 2020-06-04 13:48

 **/
public class InterfaceApplication {


    public static void run(Class... args) throws IOException {
        System.out.println("--------------------------------");
        if (Objects.isNull(args) || args.length < 1) {
            System.out.println("-------------------------------请传入数据");
            return;
        }
        for (Class clazz : args) {

//        Class clazz = GoodsMerchantCategoryController.class;
            Annotation requestMappingAnnotation = clazz.getAnnotation(RequestMapping.class);
            if (Objects.isNull(requestMappingAnnotation)) {
                System.out.println("--------------------获取请求路径注解失败，请检查");
                return;
            }
            RequestMapping requestMapping = (RequestMapping) requestMappingAnnotation;
            String catName = requestMapping.name();
            String[] basePath = requestMapping.value();
            if (StringUtils.isBlank(catName) || basePath == null || basePath.length < 1) {
                System.out.println("--------------------RequestMapping.name 或者 basePath 不存在" + catName + basePath);
                return;
            }
            //判断这个分类存不存在，如果存在直接把这个catid赋值给下面
            Document interfaceCatByName = MongoDbOperator.findInterfaceCatByName(catName);
            Long catId = System.currentTimeMillis();
            if (Objects.isNull(interfaceCatByName)) {
                System.out.println("----该分类" + catName + "不存在，重新生成----");
                interfaceCatByName = new Document();
                interfaceCatByName.put("_id", catId);
                interfaceCatByName.put("index", 0);
                interfaceCatByName.put("name", catName);
                interfaceCatByName.put("project_id", InterfaceConfig.projectId);
                interfaceCatByName.put("desc", null);
                interfaceCatByName.put("uid", InterfaceConfig.uId);
                interfaceCatByName.put("add_time", catId);
                interfaceCatByName.put("up_time", catId);
                interfaceCatByName.put("__v", 0);
                boolean b = MongoDbOperator.insertInterfaceCat(interfaceCatByName);
                if (!b) {
                    System.out.println("----该分类" + catName + "不存在，重新生成失败----");
                }
            } else {
                catId = Long.valueOf(interfaceCatByName.get("_id").toString());
            }

            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                System.out.println("-------------------" + method.getName());
                Annotation[] annotations = method.getAnnotations();

                if (Objects.isNull(annotations) || annotations.length < 1) {
                    continue;
                }
                for (Annotation annotation : annotations) {
                    if (annotation instanceof PostMapping) {
                        PostMapping postMatting = ((PostMapping) annotation);
                        String name = postMatting.name();
                        String[] path = postMatting.value();
                        String allPath = basePath[0] + "/" + path[0];
                        if (StringUtils.isBlank(name)) {
                            System.out.println("---------------------方法上的注解PostMapping没有name没有赋值");
                        }
                        //根据全路径获取是否已经存在
                        Document interfaceByPathAndCatId = MongoDbOperator
                                .findInterfaceByPathAndCatId(Math.toIntExact(catId), allPath);
                        boolean update = false;
                        Long id = System.currentTimeMillis();
                        if (Objects.nonNull(interfaceByPathAndCatId)) {
                            System.out.println("---------------------根据全路径查询不为空，更新");
                            id = Long.valueOf(interfaceByPathAndCatId.get("_id").toString());
                            update = true;
                        }
                        //获取请求封装类
                        Class[] parameterTypes = method.getParameterTypes();
                        if (Objects.isNull(parameterTypes) || parameterTypes.length < 1) {
                            continue;
                        }
                        Document interfaceDocument = new Document();

                        HashMap reqBodyOtherMap = getReqBodyOtherMap(parameterTypes[0]);

                        interfaceDocument.put("_id", id);
                        interfaceDocument.put("edit_uid", 0);
                        System.out.println("-----------------------interfaceDocument" + interfaceDocument);

                        String string = JSON.toJSONString(reqBodyOtherMap);
                        System.out.println("reqBodyOtherMap string" + string);
                        interfaceDocument.put("req_body_other", string);

                        InterfaceWrapperOperator.wrapperInterfaceMessage(interfaceDocument, allPath, name, catId,
                                InterfaceConfig.projectId);
                        if (update) {
                            MongoDbOperator.updateInterfaceById(interfaceDocument);
                        } else {
                            MongoDbOperator.insertInterface(interfaceDocument);
                        }
                    }
                    continue;
                }
            }
        }

    }


    public static HashMap getReqBodyOtherMap(Class parameterType) throws IOException {
        HashMap reqBodyOtherMap = new HashMap<>();
        System.out.println("----------------- paramClazz" + parameterType);
        Field[] fields = parameterType.getDeclaredFields();
        String typeName = parameterType.getTypeName();
        //获取对应的必须的字段
        List<String> required = getReqBodyParamRequired(fields, parameterType);
        //获取字段注释对应关系
        Map<String, String> fieldCommentMap = JavaObjectComment.getFieldComment(typeName);

        HashMap propertiesMap = new HashMap<>();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            String columnType = InterfaceWrapperOperator.getColumnType(fieldType);
            //字段类型名称
            String fieldName = field.getName();
            if (InterfaceConfig.excludeField.contains(fieldName)) {
                continue;
            }
            HashMap<Object, Object> propertiesValueMap = new HashMap<>();
            //如果是数组，或者列表，要获取对应泛型
            if (columnType.equals("array")) {
                HashMap<Object, Object> hashMap = new HashMap<>();
                Class tClass = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (Objects.isNull(tClass)) {
                    System.out.println("-------------数组或者列表对应泛型不存在---------" + fieldType);
                    break;
                }
                String columnTypeChild = InterfaceWrapperOperator.getColumnType(tClass);
                if (columnTypeChild.equals("object")) {
                    HashMap reqBodyOtherMapChild = getReqBodyOtherMap(tClass);
                    hashMap.put("items", reqBodyOtherMapChild);
                } else {
                    hashMap.put("type", columnTypeChild);
                }
                propertiesValueMap.put("type", columnType);
                propertiesValueMap.put("items", hashMap);
            } else {
                String comment = fieldCommentMap.get(fieldName);
                propertiesValueMap.put("type", columnType);
                propertiesValueMap.put("default", "1");
                propertiesValueMap.put("description", comment);
            }
            propertiesMap.put(fieldName, propertiesValueMap);
        }
        reqBodyOtherMap.put("properties", propertiesMap);
        reqBodyOtherMap.put("type", "object");
        reqBodyOtherMap.put("required", required);
        return reqBodyOtherMap;

    }


    public static List<String> getReqBodyParamRequired(Field[] fields, Object object) {
        ArrayList<String> required = new ArrayList<>();
        for (Field field : fields) {
            //name
            String name = field.getName();
            //是否存在notNull注解,true 为必填项
            boolean notNull = isNotNull(field);
            if (notNull) {
                required.add(name);
            }
        }
        return required;
    }

    public static boolean isNotNull(Field field) {
        Annotation[] annotations = field.getAnnotations();
        return Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof NotNull);
    }

}

