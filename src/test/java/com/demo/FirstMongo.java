package com.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

public class FirstMongo {

	private MongoClient mongoClient;

	public FirstMongo() {
		MongoClientURI connectionString = new MongoClientURI("mongodb://10.19.156.250:27017");
		mongoClient = new MongoClient(connectionString);
	}

	public void insertOne() {
		try {
			// MongoClient mongoClient = new MongoClient();
			// MongoClient mongoClient = new MongoClient( "localhost" );
			// mongoClient = new MongoClient( "10.19.156.250" , 27017 );
			// mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
			// 一、连接服务器
			// MongoClientURI connectionString = new
			// MongoClientURI("mongodb://10.19.156.250:27017");
			// mongoClient=new MongoClient(connectionString);
			// 二、获取数据库
			MongoDatabase database = mongoClient.getDatabase("mydb");
			// 三、获得集合
			MongoCollection<Document> collection = database.getCollection("test");

			// 四、对集合操作
			// 1.插入文档
			Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1)
					.append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
					.append("info", new Document("x", 203).append("y", 102));

			collection.insertOne(doc);
			System.out.println("insertOne execute sucess!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void insertMutil() {
		try {

			// 二、获取数据库
			MongoDatabase database = mongoClient.getDatabase("mydb");
			// 三、获得集合
			MongoCollection<Document> collection = database.getCollection("test2");

			// 四、对集合操作
			// 1.插入文档
			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < 100; i++) {
				documents.add(new Document("i", i));
			}
			collection.insertMany(documents);
			System.out.println("insertMutil execute sucess!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void count() {
		try {
			// 二、获取数据库
			MongoDatabase database = mongoClient.getDatabase("mydb");

			// 三、获得集合
			MongoCollection<Document> collection = database.getCollection("test2");
			System.out.printf("collection %s length is %d", "test2", collection.count());
			System.out.println();

			// 例子2 带条件的count

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void find() {
		try {
			// 二、获取数据库
			MongoDatabase database = mongoClient.getDatabase("mydb");

			// 三、获得集合
			MongoCollection<Document> collection = database.getCollection("test2");
			{
				Document doc1 = collection.find().first();
				System.out.println(doc1.toJson());
			}

			{
				// 例子2 读取多条记录
				MongoCursor<Document> cursor = collection.find().iterator();
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					System.out.printf("collection %s item %s ", "test2", doc.getString("i"));
					System.out.println();
				}
			}
			
			{
				//例子 带查询条件的查询
				Block<Document> printBlock = new Block<Document>() {
				     @Override
				     public void apply(final Document document) {
				    	 System.out.printf("collection %s item %s ,json is %s", "test2", document.getString("i"),document.toJson());
				         System.out.println(document.toJson());
				     }
				};

				collection.find(or(eq("i",7),eq("i",10))).forEach(printBlock);
				
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {

	}
}
