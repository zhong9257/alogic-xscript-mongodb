alogic-xscript-mongodb
=====================

### Overview

alogic-xscript-mogodb是基于xscript2.0的mongodb插件，提供了使用mongod所需的相关指令，无缝对接mongodb的crud等实现。

### Getting started

按照以下步骤，您可轻松在您的项目中使用alogic-xscript-mongodb.

不过开始之前，我们希望您了解xscript的相关知识。

- [xscript2.0](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2.md) - 您可以了解xscript的基本原理及基本编程思路
- [xscript2.0基础插件](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2-plugins.md) - 如何使用xscript的基础插件
- [基于xscript的together](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2-together.md) - 如何将你的script发布为alogic服务

#### 增加maven依赖

您可以在[中央仓库](http://mvnrepository.com/)上找到[alogic-xscript-kvalue](http://mvnrepository.com/search?q=com.github.anylogic%3Aalogic-xscript-kvalue)的发布版本。

```xml

    <dependency>
		<groupId>org.mongodb</groupId>
		<artifactId>mongodb-driver</artifactId>
		<version>3.4.0</version>
		</dependency>
	<dependency>
		<groupId>com.github.anylogic</groupId>
		<artifactId>alogic-common</artifactId>
		<version>1.6.6-SNAPSHOT</version>
	</dependency>

```  

#### 引入Namespace

在您的脚本中，你需要引入如下Namespace，比如:

```xml
	
	<script>
    	<using xmlTag = "mg-cli" module="com.alogic.xscript.mongodb.MgClient"/>
    	
    	<mg-cli cli="globalMongoDBClientPool">
    		<!--下面是针对globalMongoDBClientPool连接的操作-->
    	</mg-cli>
    </script>
```  
或者  

```xml

	<script>
        <using xmlTag = "mg-db" module="com.alogic.xscript.mongodb.MgDB"/>

        <mg-db cli="globalMongoDBClientPool" db="mydb">
		<!--下面是针对globalMongoDBClientPool连接的mydb数据库的操作-->
	    </mg-db>
    </script>

```  
或者  
```xml

    <script>
        <using xmlTag = "mg-table" module="com.alogic.xscript.mongodb.MgTable"/>
        
    	<mg-table cli="globalMongoDBClientPool" db="mydb" table="zytest" >
    	    <!--下面是针对globalMongoDBClientPool连接的mydb数据库的文档zytest的操作-->
    		<mg-insert tagValue="insert1" many="true" doc="[{ &quot;a&quot;: &quot;aa&quot;, &quot;b&quot;: &quot;bb&quot;},{ &quot;a&quot;: &quot;ac&quot;, &quot;b&quot;: &quot;bc&quot;}]">
    				<!-- 子指令里取内容 -->
    			<doc></doc>
    		</mg-insert>
    	</mg-table>
    </script>
```  


### Example

下面的案例是对MongoDB 数据库mydb的文档zytest的操作.

```xml

	<script>
		<using xmlTag = "kv-row" module="com.alogic.xscript.kvalue.KVRow"/>
		
		<array tag="log">
			<kv-row schema="demo" table="hash" key="test">
				<kv-hmset values="id;alogic;name;ketty;note;it's a web server"/>
				
				<array-item><get id="result" value="${$kv-hmset}"/></array-item>
				
				<kv-hset key="address" value="192.168.1.23"/>
				<array-item><get id="result" value="${$kv-hset}"/></array-item>
				
				<kv-hget key="note"/>
				<array-item><get id="result" value="${$kv-hget}"/></array-item>
				
				<kv-hmget tag="mget" keys="id;name"/>
				
				<kv-hgetall tag="getall"/>
			</kv-row>
		</array>
		
	</script>	

```

为了运行上面的指令，你必须要做下列工作：
* 启动一个mongoDB服务器或者mongoDB分片集群;
* 在环境变量中配置mogoDB服务器的地址和端口，指定mongodDB连接配置文件，参考[settings.xml](src/test/resources/conf/settings.xml);
```xml

    <?xml version="1.0"?>
    <settings>
    	<parameter id="naming.master" value="java:///conf/mongo-pool.xml#Demo" />
    	<parameter id="mongod.uris" value="127.0.0.1:27017;127.0.0.1:27018" />
    </settings>

```

* 指定一个kvalue配置文件，参考[kvalue.xml](src/test/resources/conf/kvalue.xml);
```xml

<?xml version="1.0" encoding="UTF-8"?>
<naming>
	<context module="com.alogic.pool.context.Inner">
		<pool id="globalMongoDBClientPool" module="com.alogic.xscript.mongodb.connection.GlobalMongoDBClientFactory">
			<uri>${mongod.uris}</uri>
			<mongoCredentials>
				<mongoCredential username="u" password="p" coder="" authenticationDB="demo"></mongoCredential>
			</mongoCredentials>                                               
			<mongoClientOptions></mongoClientOptions>
		</pool>
	</context>
</naming>

```
在上面的配置文件中，创建一个名为demo的schema，并在其中创建了4张表(zset,hash,str,list),分别用于测试SortedSet,Hash,String,List等4种常见的redis数据类型。

做好上面的工作之后，可以运行[demo](src/test/java/Demo.java)来测试xscript脚本。

### Reference

参见[alogic-xscript-mongodb](src/docs/reference.md)。

### History

- 1.0.1 [20161223 zhongyi]
	+ 初次发布


