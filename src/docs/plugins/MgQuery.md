# /self/passport/Del

## 概述

查看指定数据库表中的文档

服务的路径如下：
```
/self/passport/Del
```

## 更新历史

[20160729 cenwan] 创建本文档

> 开发任务说明：
> * 为当前用户删除指定的通行证
> * 记录审计日志（参见util_audit_log表）
> * 相关数据库表：user_passport

<hr>

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | loginId | String | 通行证ID | 必须 |
| 1 | id | String | 用户ID | 必须 |

## 输入文档
无

## 输出文档
无

## 样例

下面是一个样例，
服务地址如下：
```
http://localhost:9005/services/self/passport/Del?loginId=bbb&id=bbb
```

输出结果：
```
{
    "duration": "192", 
    "host": "0:0:0:0:0:0:0:1:9005", 
    "reason": "It is successful", 
    "code": "core.ok", 
    "serial": "BSCH2B14TU", 
}
```

## 异常
返回相应的信息，并返回代码：core.ok

## 实现

### 服务配xscrit2.0配置而来，具体实现代码如下：
```
<?xml version="1.0" encoding="utf-8"?>
<script>
	<!-- 服务说明： 为当前用户删除指定通行证 -->
	<using xmlTag="idu" module="com.alogic.together.idu.IDU" />
	<using xmlTag="checkRole" module="com.alogic.common.xscript.service.CheckRole" />
	<using xmlTag="bizlog" module="com.alogic.common.xscript.service.Bizlog" />

	<!-- 初始化一个缺省的当前登录用户 -->
	<checkAndSet id="id" dft="anonymous" />

	<idu>
		<db dbcpId="default">
			<!-- 删除指定通行证 -->
			<delete
				sql.Delete="
                  DELETE 
                  FROM
                      user_passport 
                  WHERE 
                      login_id = #{bind('loginId')}
                  AND 
                  	  user_id = #{bind('id')}	
              " />
			<!-- 记录审计日志 -->
			<bizlog type="User" id="${id}" content="用户${id}删除了通行证${loginId}" />
		</db>
		
		<!-- 清除用户缓存 -->
		<cache cacheId="users">
			<expire id="${id}" />
		</cache>
	</idu>
</script>
```

### 相关数据库
本服务主要是对用户通行证表(user_passport)做删除操作，详见itportal数据库模型

本服务SQL语句如下：
```
	  DELETE 
	  FROM
	      user_passport 
	  WHERE 
	      login_id = #{bind('loginId')}
	  AND 
	  	  user_id = #{bind('id')}
```

### 数据源
采用内置的dbcp数据源itportal

### 业务日志
* 本服务属于删除服务，记录服务日志，entity_type为User
* 记录审计日志（参见util_audit_log表）