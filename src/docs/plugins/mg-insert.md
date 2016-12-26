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
| 3 | doc | doc,待插入的文档，支持计算，缺省为""|
| 4 | docNode | 待插入的文档内容来自docNode指定的子标签返回的数据集中节点docResultNode，缺省为"doc"|
| 5 | docResultNode |  待插入的文档内容来自docNode指定的子标签返回的数据集中节点docResultNode，缺省为"doc"|
| 6 | many | true,表示插入多个文档；false表示插入单个文档；缺省为false |
| 7 | idKey | 指定待插入文档的ObjectId,缺省为_id |

### 案例

参考[mg-insert操作案例](case.set.md)