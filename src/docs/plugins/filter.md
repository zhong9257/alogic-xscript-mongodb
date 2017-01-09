filter
======

filter：过滤器，一组用于过滤文档的操作集。

> 本指令对应mongodb的操作指令[Filters](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/?com/mongodb/client/model/Filters.html)

### 实现类

com.alogic.xscript.mongodb.util.filter.*

### 操作集

支持以下操作：[详细参考](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/?com/mongodb/client/model/Filters.html)：

| 编号 | 指令 | 说明 | 
| ---- | ---- | ---- | 
| 1 | And | 逻辑与  |
| 2 | Eq | 等于  |
| 3 | Exists | 是否存在给定字段  |
| 4 | Gt | 大于  |
| 5 | Gte | 大于或等于  |
| 6 | In | 返回与数组中条件匹配的文档  |
| 7 | Lt | 小于  |
| 8 | Lte | 小于或等于  |
| 9 | Mod | 求模  |
| 10 | Ne | 不等于  |
| 11 | Nin | 返回与数组中所有条件都不匹配的文档  |
| 12 | Nor | 逻辑或非  |
| 13 | Not | 返回不匹配的文档  |
| 14 | Or | 逻辑或  |

### 操作方式

通常用于查询、更新、插入等操作，将filter标签置入到这些操作标签内部，作为子标签使用，filter标签格式如下：

```

	<filter module="指令" field="name" value="value"></filter>

```

#### 示例：

> 过滤条件：对要查询的文档进行过滤，返回"name"="Burger Buns"的文档

```

	<mg-query first="false" projection="name">
		<filter module="Eq" field="name" value="Burger Buns"></filter>
	</mg-query>

```