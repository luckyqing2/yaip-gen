package com.yapi.cloud.api.doc.generator.gen;

import java.util.HashSet;
import java.util.Set;

/**
 * @version 8.0.0
 * @program: interface-gen
 * @author: "清歌"
 * @create: 2020-06-09 10:14

 */
public class InterfaceConfig {


    /**
     * 项目ID
     */
    public static Integer projectId = 11;

    /**
     * 用户id
     */
    public static Integer uId = 11;

    /**
     * 项目的路径-用于找到。java文件
     */
    public static String PATH = "D:\\ideaworkspace\\mamaezhan-cloud-mixcontroller\\mamaezhan-cloud-controller"
            + "-business\\src\\main\\java\\";

    public static Set<String> excludeField = new HashSet<>();

    static {
        excludeField.add("userId");
        excludeField.add("merchantId");

    }


    public static class MongoDbConfig{

        /**
         * 数据库名称
         */
        public static String dbName = "yapi";

        /**
         * 连接地址
         */
        public static String dbHost = "127.0.0.1";

        /**
         * db数据库端口
         */
        public static Integer dbPort = 27017;

        /**
         * 用户名
         */
        public static String dbUserName = "yapi";

        /**
         * 密码
         */
        public static String dbPwd = "123456";
        /**
         * 接口文档的表（collection）
         */
        public static String interfaceName = "interface";
        /**
         * 目录表(collection)
         */
        public static String interfaceCatName = "interface_cat";


    }


}
