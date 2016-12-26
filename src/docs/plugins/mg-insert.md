mg-insert.md
=======

mg-insert.将一个或多个document添加到集合当中，返回被添加到集合中的文档(包括自动生成的ObjectId)，

### 实现类

com.alogic.xscript.mongodb.MgInsert

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的集合id，缺省为$mg-table |
| 2 | tag | 当前文档的节点,支持计算,缺省为$mg-insert |
| 3 | doc | 待插入的文档,符合json格式的字符串,支持计算，缺省为""|
| 4 | docNode | 待插入的文档内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docNode缺省为"doc"|
| 5 | docResultNode |  待插入的文档内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docResultNode缺省为"doc"|
| 6 | many | true,表示插入多个文档；false表示插入单个文档；缺省为false |
| 7 | idKey | 指定待插入文档的ObjectId,缺省为_id |

>doc和docNode两种方式二选一；当doc存在且不为空，docNode也存在时；待插入文档内容为doc生效。

### 案例

现有下列脚本

、、、xml
<script>
	<using xmlTag = "mg-cli" module="com.alogic.xscript.mongodb.MgClient"/>
	<using xmlTag = "mg-db" module="com.alogic.xscript.mongodb.MgDB"/>
	<using xmlTag = "mg-table" module="com.alogic.xscript.mongodb.MgTable"/>
	
 	<mg-cli cli="globalMongoDBClientPool">
		<mg-db db="demo">
			<mg-table table="test" >
				<mg-insert tag="insert-cli" many="false" doc="{ &quot;a&quot;: &quot;aa-cli&quot;, &quot;b&quot;: &quot;bb-cli&quot;}">
				</mg-insert>
			</mg-table>
		</mg-db>
	</mg-cli>
	
	

	<mg-db cli="globalMongoDBClientPool" db="demo">
		<mg-table table="test" >
			<mg-insert tag="insert-db" many="true" doc="[{ &quot;a&quot;: &quot;aa-db&quot;, &quot;b&quot;: &quot;bb-db&quot;},{ &quot;a&quot;: &quot;ac-db&quot;, &quot;b&quot;: &quot;bc-db&quot;}]">
			</mg-insert>
		</mg-table>
	</mg-db>

	
	
	<mg-table cli="globalMongoDBClientPool" db="demo" table="test" >
		<mg-insert tag="insert-table" many="true" docNode="doc" docResultNode="doc">
				<!-- 子指令里取内容 -->
			<doc>
				<set id="array" value="tom;jerry;alogic;ketty"/>
				<array tag="doc">			
                    <foreach in="${array}">
                            <array-item>
                                    <get id="a" value="${$value}"/>
                                    <get id="b" value="${$value}"/>
                            </array-item>
                    </foreach>
            </array>
			</doc>
		</mg-insert>
	</mg-table>
	
</script>
、、、  

结果

、、、json
	{
    "insert-table": [
        {
            "b": "tom", 
            "a": "tom", 
            "_id": "58608ec4d20a622888fe5b8d"
        }, 
        {
            "b": "jerry", 
            "a": "jerry", 
            "_id": "58608ec4d20a622888fe5b8e"
        }, 
        {
            "b": "alogic", 
            "a": "alogic", 
            "_id": "58608ec4d20a622888fe5b8f"
        }, 
        {
            "b": "ketty", 
            "a": "ketty", 
            "_id": "58608ec4d20a622888fe5b90"
        }
    ], 
    "insert-db": [
        {
            "a": "aa-db", 
            "b": "bb-db", 
            "_id": "58608ec4d20a622888fe5b8b"
        }, 
        {
            "a": "ac-db", 
            "b": "bc-db", 
            "_id": "58608ec4d20a622888fe5b8c"
        }
    ], 
    "insert-cli": {
        "a": "aa-cli", 
        "b": "bb-cli", 
        "_id": "58608ec4d20a622888fe5b8a"
    }
}	
、、、
