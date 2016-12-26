mg-2dspherequery
======

mg-2dspherequery：地理查询，用于二位地理空间位置的查询。
> 概念说明
> * 地理空间：文档中必须带有坐标信息，即x,y值，用来标识对象的方位。

> 本指令对应mongodb的操作指令[collection.find]()

### 实现类

com.alogic.xscript.mongodb.MMg2dsphereQuery

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |操作的集合名，位于mg-table标签|
| 3 | tagValue | 自定义 |返回的标志|
| 4 | mode | inPolygon,inBox,inCircle,near |地理查询的方式，即多边形、长方形、圆形区域内部是否有文档对象存在，near查询指定地点附近范围内是否有对象存在|
| 5 | field | 区域参数，自定义 |格式：第一个参数是要查询的键；之后的参数是数值（多边形是一对对坐标的x,y值；长方形是左下角到右上角的x,y值；圆形是圆形坐标x,y值和半径；near参数是指定地点的x,y值，距离指定地点的最大值max和最小值min）|

### 案例

输出结果：
```

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
				<mg-2dspherequery tagValue="data" mode="inCircle" field="loc,0,0,10">
				</mg-2dspherequery>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```