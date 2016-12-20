package com.alogic.xscript.mongodb.connection;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.alogic.pool.impl.Singleton;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


public class GlobalMongoDBClientFactory extends Singleton{
	
	private MongoClient mongoClient = null;
	private List<ServerAddress> seeds;
	private List<MongoCredential> credentialsList;
	private MongoClientOptions options;
	
	private ServerAddress address;
	
	
	@Override
	public void configure(Properties p) {
		// TODO Auto-generated method stub
		//System.out.println("1");
	}
	
	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		if(null==mongoClient){
			synchronized(GlobalMongoDBClientFactory.class){
				if(null==mongoClient){					
					//解析uri,支持单服务器或者副本集和分片集群。uri格式：host:port,host:port
					Element node = XmlTools.getFirstElementByPath(e, "uri");
			        String uri=node.getTextContent();
			        String[] uris=uri.trim().split(",");
			        if(uris.length>1){
			        	seeds=new ArrayList<ServerAddress>();
			        	for (int i = 0; i < uris.length; i++) {
							String[] _temp = uris[i].split(":");
							ServerAddress sa=new ServerAddress(_temp[0], Integer.parseInt(_temp[1]));
							seeds.add(sa);
						}
			        }else{
			        	String[] _temp = uri.split(":");
			        	address=new ServerAddress(_temp[0], Integer.parseInt(_temp[1]));
			        }
			        
			        //to-do 验证 option
			        
			        mongoClient=new MongoClient(address);
				}
			}
		}
		//System.out.println("2");
	}

	@Override
	protected <pooled> pooled createObject(int priority, int timeout) {
		return (pooled)mongoClient;
	}
	
	

}
