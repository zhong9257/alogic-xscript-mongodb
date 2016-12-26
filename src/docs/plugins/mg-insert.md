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

>doc和docNode两种方式二选一；当doc存在且不为空，docNode也存在是；待插入文档内容为doc生效。

### 案例

参考[mg-insert操作案例](case.insert.md)