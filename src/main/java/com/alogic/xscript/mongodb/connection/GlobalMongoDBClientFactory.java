package com.alogic.xscript.mongodb.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.pool.impl.Singleton;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDriverInformation;


/**MongoDBClient工厂，单例模式生产MongoDBClient
 * @author zhongyi
 *
 */
public class GlobalMongoDBClientFactory extends Singleton{
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger 
		= LogManager.getLogger(GlobalMongoDBClientFactory.class);	
	
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
		
		Class[] paramClasses=new Class[4];
		paramClasses[1]=List.class;
		paramClasses[2]=MongoClientOptions.class;
		paramClasses[3]=MongoDriverInformation.class;
		
		Object[] paramObjects=new Object[4];
		
		if(null==mongoClient){
			synchronized(GlobalMongoDBClientFactory.class){
				if(null==mongoClient){					
					//解析uri,支持单服务器或者副本集和分片集群。uri格式：host:port,host:port
					Element node = XmlTools.getFirstElementByPath(e, "uri");
			        String uri=node.getTextContent();
			        uri=PropertiesConstants.getString(props, uri,uri);
			        String[] uris=uri.trim().split(",");
			        if(uris.length>1){
			        	
			        	seeds=new ArrayList<ServerAddress>();
			        	for (int i = 0; i < uris.length; i++) {
							String[] _temp = uris[i].split(":");
							ServerAddress sa=new ServerAddress(_temp[0], Integer.parseInt(_temp[1]));
							seeds.add(sa);
						}
			        	paramClasses[0]=List.class;
			        	paramObjects[0]=seeds;
			        }else{
			        	
			        	String[] _temp = uri.split(":");
			        	address=new ServerAddress(_temp[0], Integer.parseInt(_temp[1]));
			        	paramClasses[0]=ServerAddress.class;
			        	paramObjects[0]=address;
			        }
			        
			        
			        
			        Element mongoCredentials = XmlTools.getFirstElementByPath(e, "mongoCredentials");
			        if(null!=mongoCredentials){
			        	NodeList mongoCredentialNodeList=XmlTools.getNodeListByPath(mongoCredentials,"mongoCredential");
			        	credentialsList=new ArrayList<>();
			        	if(null!=mongoCredentialNodeList&&mongoCredentialNodeList.getLength()>0){
			        		credentialsList=new ArrayList<>();
			        		for (int i = 0; i < mongoCredentialNodeList.getLength(); i++) {
			        			Node n=mongoCredentialNodeList.item(i);
			        			Element ele = (Element)n;
			        			String username = ele.getAttribute("username");
			        			String password=ele.getAttribute("password");
			        			String coder=ele.getAttribute("coder");
			        			String authenticationDB=ele.getAttribute("authenticationDB");
			        			username = PropertiesConstants.getString(props, username,username);
			        			password = PropertiesConstants.getString(props, password,password);
			        			authenticationDB = PropertiesConstants.getString(props, authenticationDB,authenticationDB);
			        			
			        			String _password = password;
			    				if (coder != null && coder.length() > 0){
			    					//通过coder进行密码解密
			    					try {
			    						Coder _coder = CoderFactory.newCoder(coder);
			    						_password = _coder.decode(password, username);
			    					}catch (Exception ex){
			    						logger.error("Can not find coder:" + coder);
			    					}
			    				}
			    				MongoCredential mongoCredential=MongoCredential.createCredential(username, authenticationDB, _password.toCharArray());
			    				credentialsList.add(mongoCredential);
			    				
							}
			        	}
			        	
			        }
			        paramObjects[1]=credentialsList;
			        

			        paramObjects[2]=MongoClientOptions.builder().build();
			        paramObjects[3]=MongoDriverInformation.builder().build();

			        //to-do 验证 option
			        Class<?> class1 = null;
			        try {			        	
						class1 =Settings.getClassLoader().loadClass("com.mongodb.MongoClient");;
					} catch (ClassNotFoundException e1) {
						logger.error(e1);
					}
			        
			        Constructor<MongoClient> constructor=null;
			        try {
			        	constructor=(Constructor<MongoClient>) class1.getConstructor(paramClasses);
			        	mongoClient=constructor.newInstance(paramObjects);
					} catch (NoSuchMethodException e1) {
						
						logger.error(e1);
					} catch (SecurityException e1) {
						logger.error(e1);
					} catch (InstantiationException e1) {
						logger.error(e1);
					} catch (IllegalAccessException e1) {
						logger.error(e1);
					} catch (IllegalArgumentException e1) {
						logger.error(e1);
					} catch (InvocationTargetException e1) {
						logger.error(e1);
					}
			        
			        
			        
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
