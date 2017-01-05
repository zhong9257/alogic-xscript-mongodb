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

您可以在[中央仓库](http://mvnrepository.com/)上找到[alogic-xscript-mongodb](http://mvnrepository.com/search?q=com.github.anylogic%3Aalogic-xscript-kvalue)的发布版本。

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

下面的案例是对MongoDB 数据库demo的demotest集合的操作.

```xml

<?xml version="1.0"?>
<script>
	<using xmlTag="mg-cli" module="com.alogic.xscript.mongodb.MgClient" />
	<using xmlTag="mg-db" module="com.alogic.xscript.mongodb.MgDB" />
	<using xmlTag="mg-table" module="com.alogic.xscript.mongodb.MgTable" />





	<mg-db cli="globalMongoDBClientPool" db="demo">

		<mg-table cli="globalMongoDBClientPool" db="demo" table="demotest">
			<mg-drop tag="drop1">
			</mg-drop>
		</mg-table>


		<!-- create collection demotest -->
		<mg-tablenew table="demotest"></mg-tablenew>

		<mg-table cli="globalMongoDBClientPool" db="demo" table="demotest">
			<!-- insert records to demotest -->
			<mg-insert tag="insert1" many="false"
				doc="{ &quot;a&quot;: &quot;aa-cli&quot;, &quot;num&quot;: 5}">
			</mg-insert>
			<mg-insert tag="insert2" many="true" docNode="doc"
				docResultNode="doc">
				<!-- 子指令里取内容 -->
				<doc>
					<set id="array" value="tom;jerry;alogic;ketty" />
					<array tag="doc">
						<foreach in="${array}">
							<array-item>
								<get id="a" value="${$value}" />
								<get id="num" value="22" />
							</array-item>
						</foreach>
					</array>
				</doc>
			</mg-insert>
			<mg-insert tag="insert3" many="true"
				doc="[{ &quot;a&quot;: &quot;aa-db&quot;, &quot;num&quot;: 22},{ &quot;a&quot;: &quot;ac-db&quot;, &quot;num&quot;:10}]">
			</mg-insert>

			<!-- query records -->
			<mg-query tag="query1" first="false" limit="100" offset="1"
				projection="a,num">
			</mg-query>

			<!-- update special records -->
			<mg-update many="true" doc="{$inc:{num:100}}">
				<filter module="And">
					<filter module="Eq" field="a" value="aa-cli"></filter>
					<filter module="Eq" field="num" value="305" type="integer"></filter>
				</filter>
			</mg-update>

			<!-- query records -->
			<mg-query tag="afterUpdate" first="false" limit="2" offset="0"
				projection="a,num">
				<filter module="Eq" field="a" value="ketty"></filter>
			</mg-query>
		</mg-table>


	</mg-db>

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

* 指定一个mongo-pool配置文件，参考[mongo-pool.xml](src/test/resources/conf/mongo-pool.xml);
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
在上面的配置文件中，创建一个名为globalMongoDBClientPool的连接池，并在设置了服务器地址${mongod.uris},设置了验证信息。

做好上面的工作之后，可以运行[demo](src/test/java/Demo.java)来测试xscript脚本。

### Reference

参见[alogic-xscript-mongodb](src/docs/reference.md)。

### History

- 1.0.1 [20161223 zhongyi]
	+ 初次发布


