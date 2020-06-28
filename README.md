###YAPI接口文档生成相应的目录及接口工程

####[配合YAPI接口生成接口文档工具项目地址](https://github.com/luckyqing2/yaip-gen)

####文件解说：
  - InterfaceConfig，配置文件的存放
    - projectId ：项目ID(通过页面新增的项目)
    - uId ： 用户id
    - PATH ：项目的路径-用于找到。java文件
    - excludeField ：  需要排除的字段
    - MongoDbConfig
        - dbName ：数据库名称
        - dbHost ：连接地址
        - dbPort ：db数据库端口
        - dbUserName ：用户名
        - dbPwd ：数据库密码
        - interfaceName ：接口文档的表（collection）
        - interfaceCatName ：目录表(collection)
        
        
#### 下载打包引用 
```
    <dependency>
        <groupId>com.mamaezhan.cloud</groupId>
        <artifactId>api-doc-generator</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
  
```
    
#### 新建一个java文件或者新增一个main方法

~~~
 public static void main(String[] args) {
        //设置java文件的路径
        InterfaceConfig.PATH = "D:\\ideaworkspace\\mamaezhan-cloud-mixcontroller\\mamaezhan-cloud-controller-business"
                + "\\src\\main\\java\\";
        //设置对应的项目ID
        InterfaceConfig.projectId = 11;

        //设置对应的用户ID
        InterfaceConfig.uId = 11;
        //mongodb数据库设置，这里都先使用默认的就好
        //MongoDbConfig.dbPwd = "123456";

        try {
            InterfaceApplication.run(GoodsMerchantCategoryController.class);
        } catch (IOException e) {
            System.out.println("---------------------异常：" + e);
        }

    }
~~~


####以下是具体要求
   1. 这里只支持JSON格式生成，并且是POST
   2. PostMapping name属性就是为接口文档的名称，类上的RequestMapping name 为分类名称，作为唯一值，如果不唯一就会被覆盖
   3. 每个接收的类，都必须有类注释，属性注释，不然会对应不上，并且是以/**开始 */结束，导致错误
   4. 会检查属性是否有@NotPoint注解,如果有是必须填写的
   
   一个完整的接收类
   ~~~
        
        package com.***.cloud.controller.business.interfaces.controller.goods.dto.ao;
        
        import javax.validation.constraints.NotNull;
        import lombok.Data;
        
        /**
         * @version 8.0.0
         * @program: 
         * @author: "清歌"
         * @create: 2020-05-20 14:52
         */
        @Data
        public class ExchangeMerchantCategorySortNoAo {
        
            /**
             * 需要移动的后台类目Id
             */
            @NotNull(message = "需要移动的前台类目ID不能为空")
            private Long moveableMerchantCategoryId;
        
            /**
             * 要替换的后台类目ID
             */
            @NotNull(message = "需要替换的前台类目ID不能为空")
            private Long replaceMerchantCategoryId;
        
            /**
             * 用户ID
             */
            private Long userId;
        
            /**
             * 商户ID
             */
            private Long merchantId;
        }

   ~~~