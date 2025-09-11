# Admin Management System API 文档

欢迎使用 Admin Management System API 文档！

## 🚀 系统介绍

Admin Management System 是一个基于 Spring Boot 3.x 开发的现代化后台管理系统，采用最新的技术栈和架构设计，为企业级应用提供完整的用户管理、权限控制、系统配置等核心功能。

## 📋 主要功能

### 🔐 认证授权
- JWT Token 认证
- 基于 RBAC 的权限控制
- 登录限制和账户锁定
- Token 黑名单管理

### 👥 用户管理
- 用户基础信息管理
- 用户状态控制
- 密码重置功能
- 用户角色分配

### 🛡️ 角色权限
- 角色管理
- 菜单权限配置
- 角色菜单关联
- 细粒度权限控制

### 📖 字典管理
- 字典类型管理
- 字典数据维护
- 支持缓存优化

### 🔧 系统管理
- 缓存管理
- 在线用户监控
- 系统配置

## 🛠️ 技术栈

- **框架**: Spring Boot 3.2.1
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.5
- **缓存**: Redis + Spring Cache
- **安全**: Spring Security 6.x + JWT
- **文档**: Knife4j 4.4.0 + OpenAPI 3.0
- **工具**: Hutool 5.8.24 + MapStruct 1.5.5

## 📚 API 使用说明

### 🔑 认证方式

本系统使用 JWT Token 进行身份认证，请在调用需要认证的接口时，在请求头中添加：

```
Authorization: Bearer <your-jwt-token>
```

### 🏷️ 接口分组

API 接口按功能模块进行分组：

- **01-系统管理**: 系统级别的配置和管理接口
- **02-用户管理**: 用户信息的增删改查操作
- **03-角色权限**: 角色和权限相关的管理接口
- **04-字典管理**: 系统字典的维护操作
- **05-认证授权**: 登录认证和权限验证相关接口
- **06-基础设施**: 基础设施和工具类接口
- **07-监控运维**: 系统监控和运维管理接口

### 📝 响应格式

所有接口都遵循统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### ⚠️ 错误处理

当请求出现错误时，系统会返回标准的错误响应：

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/admin-api/system/users"
}
```

常见的 HTTP 状态码说明：

- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权访问（Token 无效或已过期）
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 🎯 快速开始

1. **获取 Token**: 调用 `/admin-api/system/auth/login` 接口进行登录
2. **设置认证**: 在 Swagger UI 右上角点击 "Authorize" 按钮，输入获得的 Token
3. **调用接口**: 现在可以正常调用需要认证的接口了

## 🔗 相关链接

- [GitHub 仓库](https://github.com/admin/admin-backend)
- [在线文档](http://localhost:8080/doc.html)
- [API 接口](http://localhost:8080/v3/api-docs)

## 💡 开发说明

本文档基于 Knife4j 4.4.0 生成，支持实时调试和测试。如有任何问题或建议，请联系开发团队。

---

*最后更新时间: 2024-01-15*