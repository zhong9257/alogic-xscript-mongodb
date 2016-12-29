mg-2dspherequery
======

mg-2dspherequery：地理查询，用于二位地理空间位置的查询。
> 概念说明
> * 地理空间：文档中必须带有坐标信息，即x,y值，用来标识对象的方位。

> 本指令对应mongodb的操作指令[collection.find](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/geospatial-search/)

### 实现类

com.alogic.xscript.mongodb.Mg2dsphereQuery

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |操作的集合名，位于mg-table标签|
| 3 | tagValue | 自定义 |返回的标志| 
| 4 | mode | inPolygon,inBox,inCircle,near |地理查询的方式，即多边形、长方形、圆形区域内部是否有文档对象存在，near查询指定地点附近范围内是否有对象存在.[详细参考](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/com/mongodb/client/model/Filters.html#geoWithin-java.lang.String-org.bson.conversions.Bson-)|
| 5 | field | 区域参数，自定义 |格式：第一个参数是要查询的键；之后的参数是数值（多边形是一对对坐标的x,y值；长方形是左下角到右上角的x,y值；圆形是圆形坐标x,y值和半径；near参数是指定地点的x,y值，距离指定地点的最大值max和最小值min）.[详细参考](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/com/mongodb/client/model/Filters.html#geoWithin-java.lang.String-org.bson.conversions.Bson-)|

### 案例
实验在数据库test，集合sphere上进行测试，测试数据如下：

| _id | loc | name | category |
| ---- | ---- | ---- | ---- |
| ObjectId("5858e5ae0546ca21ecfda58c") | "type":"Point","coordinates":[-73.97,40.77] | Central Park | Parks | 
| ObjectId("5858e5e00546ca21ecfda58d") | "type":"Point","coordinates":[-73.88,40.78] | La Guardia Airport | Airport |
| ObjectId("5858e64c0546ca21ecfda58e") | "type":"Point","coordinates":[0,0] | Center | Buildings |
| ObjectId("5858e67b0546ca21ecfda58f") | "type":"Point","coordinates":[11.0,11.0] | baiyun | Airport |
| ObjectId("5858e6930546ca21ecfda590") | "type":"Point","coordinates":[-11.0,-10.0] | tianhe | Airport |
| ObjectId("5858e6be0546ca21ecfda591") | "type":"Point","coordinates":[5.0,5.0] | kawu | Airport |
| ObjectId("5858e6d20546ca21ecfda592") | "type":"Point","coordinates":[-5.0,5.0] | sky | Airport |
| ObjectId("5858e6d20546ca21ecfda593") | "type":"Point","coordinates":[10.0,5.0] | bird | Airport |
| ObjectId("5858e6d20546ca21ecfda594") | "type":"Point","coordinates":[3.0,-5.0] | tree | Airport |
| ObjectId("5858e6d20546ca21ecfda595") | "type":"Point","coordinates":[20.0,15.0] | flower | Airport |

> 地理查询条件：使用圆形范围查找，圆心为(0.0, 0.0),范围半径为5(0=<r<5)

输出结果：
```
{
    "data": [
        {
            "_id": "5858e64c0546ca21ecfda58e", 
            "loc": {
                "type": "Point", 
                "coordinates": [
                    0, 
                    0
                ]
            }, 
            "name": "Center", 
            "category": "Buildings"
        }
    ]
}

```
#### 实现

具体实现代码如下：
```
<?xml version="1.0"?>
<script>
	<using xmlTag = "mg-cli" module="com.alogic.xscript.mongodb.MgClient"/>
	<mg-cli cli="globalMongoDBClientPool">
		<mg-db db="test">
			<mg-table table="sphere" >
				<mg-2dspherequery tag="data" mode="inCircle" field="loc,0,0,5">
				</mg-2dspherequery>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```