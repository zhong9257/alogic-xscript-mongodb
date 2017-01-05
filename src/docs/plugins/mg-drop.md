mg-drop.md
=======

mg-drop.drop一个集合，drop结果返回到当前文档tag的值指定的节点。

### 实现类

com.alogic.xscript.mongodb.MgDrop

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的集合id，缺省为$mg-table |
| 2 | tag | 当前文档的节点,支持计算,缺省为$mg-drop |




### 案例

现有下列脚本

```xml
<script>
	<using xmlTag="mg-table" module="com.alogic.xscript.mongodb.MgTable" />

	<mg-table cli="globalMongoDBClientPool" db="demo" table="test" >
		<mg-drop tag="drop1">
		</mg-drop>
	</mg-table>
</script>
```

结果

```json
	{"drop1":{"drop":true,"collection":"test","database":"demo"}}
```
