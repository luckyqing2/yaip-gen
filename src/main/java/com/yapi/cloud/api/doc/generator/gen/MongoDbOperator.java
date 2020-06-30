package com.yapi.cloud.api.doc.generator.gen;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bson.Document;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import com.yapi.cloud.api.doc.generator.gen.InterfaceConfig.MongoDbConfig;

/**
 * @version 8.0.0
 * @program: api-doc-generator
 * @author: "清歌"
 * @create: 2020-06-05 14:09

 */
public class MongoDbOperator {

    private MongoDatabase mongoDatabase;


    public static Document findInterfaceCatByName(String name) {
        MongoCollection mongoCollection = MongoDbOperator
                .getMongoCollection(MongoDbConfig.dbName, MongoDbConfig.interfaceCatName);
        if (Objects.nonNull(mongoCollection)) {
            BasicDBObject query3 = new BasicDBObject();
            query3.put("name", name);
            FindIterable<Document> path = mongoCollection.find(query3).limit(1);
            MongoCursor<Document> mongoCursor = path.iterator();
            while (mongoCursor.hasNext()) {
                return mongoCursor.tryNext();
            }
        }
        return null;
    }


    public static Document findInterfaceByPathAndCatId(Long catid,String path) {
        MongoCollection mongoCollection = MongoDbOperator
                .getMongoCollection(MongoDbConfig.dbName, MongoDbConfig.interfaceName);
        if (Objects.nonNull(mongoCollection)) {
            BasicDBObject query3 = new BasicDBObject();
            query3.put("path", path);
            query3.put("catid", catid);
            FindIterable<Document> result = mongoCollection.find(query3).limit(1);
            MongoCursor<Document> mongoCursor = result.iterator();
            while (mongoCursor.hasNext()) {
                return mongoCursor.tryNext();
            }
        }
        return null;
    }


    public static boolean insertInterface(Document document) {
        MongoCollection mongoCollection = MongoDbOperator
                .getMongoCollection(MongoDbConfig.dbName, MongoDbConfig.interfaceName);
        if (Objects.nonNull(mongoCollection)) {
            mongoCollection.insertOne(document);
            return true;
        }
        return false;
    }


    public static boolean updateInterfaceById(Document document) {
        MongoCollection mongoCollection = MongoDbOperator
                .getMongoCollection(MongoDbConfig.dbName, MongoDbConfig.interfaceName);
        if (Objects.nonNull(mongoCollection)) {
            UpdateResult updateResult = mongoCollection.replaceOne(Filters.eq("_id", document.get("_id")), document);
            return updateResult.getModifiedCount() > 0;
        }
        return false;
    }

    public static boolean insertInterfaceCat(Document document) {
        MongoCollection mongoCollection = MongoDbOperator
                .getMongoCollection(MongoDbConfig.dbName, MongoDbConfig.interfaceCatName);
        if (Objects.nonNull(mongoCollection)) {
            mongoCollection.insertOne(document);
            return true;
        }
        return false;
    }


    public static MongoClient getMongoClient(String dbName) {
        //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress(MongoDbConfig.dbHost, MongoDbConfig.dbPort);
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        addrs.add(serverAddress);

        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential
                .createScramSha1Credential(MongoDbConfig.dbUserName, dbName, MongoDbConfig.dbPwd.toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);

        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(serverAddress, credentials);
        return mongoClient;
    }

    public static MongoCollection getMongoCollection(String dbName, String collectionName) {
        MongoClient mongoClient = MongoDbOperator.getMongoClient(dbName);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        MongoCollection<Document> anInterface = mongoDatabase.getCollection(collectionName);
        return anInterface;
    }

}
