# Admin Backend - 企业级管理系统后端

[![Java](https://img.shields.io/badge/Java-21-brightgreen.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.5-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 📋 项目简介

基于 Java 21 + Spring Boot 3.x 的现代化企业级管理系统后端，采用多模块单实例架构设计，集成了用户管理、权限控制、文件管理、数据导入导出等核心功能。

## ✨ 核心特性

- 🚀 **现代化技术栈**: Java 21 + Spring Boot 3.2.1 + MyBatis Plus 3.5.5
- 🏗️ **模块化架构**: 多模块设计，API-BIZ 分离，职责清晰
- 🔒 **安全体系**: Spring Security 6.x + JWT + RBAC 权限控制
- 📊 **数据处理**: EasyExcel 集成，支持大数据量导入导出
- 💾 **文件管理**: MinIO 对象存储集成，高性能文件处理
- 📖 **API 文档**: Knife4j (OpenAPI 3.0) 自动化接口文档
- 🔄 **对象映射**: MapStruct 高性能对象转换
- 🎯 **代码生成**: 基于 MyBatis Plus 的智能代码生成

## 🏗️ 架构设计

### 模块结构

```
admin-backend/
├── admin-dependencies/          # Maven BOM 依赖管理
├── admin-framework/             # 可复用 Spring Boot Starters
│   ├── admin-spring-boot-starter-excel/    # Excel 处理组件
│   ├── admin-spring-boot-starter-minio/    # MinIO 文件存储组件
│   └── admin-spring-boot-starter-mybatis/  # MyBatis Plus 增强组件
├── admin-module-system/         # 系统核心模块
│   ├── admin-module-system-api/ # API 定义 (DTOs, 接口)
│   └── admin-module-system-biz/ # 业务逻辑实现
├── admin-module-log/            # 日志管理模块
├── admin-module-infra/          # 基础设施模块
├── admin-common/                # 公共工具类和基础组件
└── admin-server/                # 主应用启动模块
```

### 核心设计模式

- **API-BIZ 分离**: 每个业务模块分为 API (接口/DTOs) 和 BIZ (实现) 子模块
- **框架 Starters**: admin-framework/ 中的自定义 Spring Boot Starters
- **分层架构**: Controller → Service → Mapper 经典三层架构
- **对象转换**: DO (数据对象) ↔ DTO (传输对象) ↔ VO (视图对象)

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 基础运行环境 |
| Spring Boot | 3.2.1 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.x+ | 缓存中间件 |
| MapStruct | 1.5.5.Final | 对象映射 |
| EasyExcel | - | Excel 处理 |
| MinIO | - | 对象存储 |
| Knife4j | 4.4.0 | API 文档 |
| Hutool | 5.8.24 | 工具库 |
| Redisson | 3.25.2 | Redis 客户端 |

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/IvanFan-sky/admin-backend.git
cd admin-backend
```

2. **配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE admin_backend CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始化脚本
mysql -u root -p admin_backend < sql/schema.sql
```

3. **配置应用**
```bash
# 修改配置文件
cd admin-server/src/main/resources/
cp application-dev.yml.example application-dev.yml
# 编辑 application-dev.yml 配置数据库连接信息
```

4. **构建运行**
```bash
# 编译项目
mvn clean compile

# 启动应用
cd admin-server
mvn spring-boot:run

# 或指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

5. **访问应用**
- 应用地址: http://localhost:8080
- API 文档: http://localhost:8080/doc.html
- 默认账户: admin / admin123

## 📝 开发指南

### 构建命令

```bash
# 完整构建
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests

# 生成源码 (包含 MapStruct 映射)
mvn generate-sources

# 运行测试
mvn test

# 依赖分析
mvn dependency:tree
```

### 代码规范

1. **实体类命名**
   - DO (Data Object): 数据库实体，位于 `dal/dataobject/`
   - DTO (Data Transfer Object): 请求响应对象，位于 `api/dto/`
   - VO (View Object): 视图对象，位于 `api/vo/`

2. **Service 层模式**
```java
// API 模块中的接口定义
public interface UserService {
    Long createUser(UserCreateDTO reqVO);
    PageResult<UserDO> getUserPage(UserPageDTO reqVO);
}

// BIZ 模块中的实现
@Service
@Validated
public class UserServiceImpl implements UserService {
    // 业务逻辑实现
}
```

3. **Controller 规范**
```java
@RestController
@RequestMapping("/admin-api/system/users")
@Tag(name = "用户管理")
@Validated
public class UserController {

    @PostMapping("/create")
    @Operation(summary = "创建用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateDTO reqVO) {
        return success(userService.createUser(reqVO));
    }
}
```

### 数据库约定

- **表命名**: 使用 `sys_` 前缀，小写+下划线分隔
- **通用字段**: 所有业务表包含 `id`, `create_time`, `update_time`, `create_by`, `update_by`, `deleted`
- **逻辑删除**: 使用 `deleted` 字段标记删除状态

## 📚 核心功能

### 🔐 权限管理
- 用户管理：用户 CRUD、状态管理、密码重置
- 角色管理：角色分配、权限绑定
- 菜单管理：动态菜单、按钮权限
- 部门管理：组织架构管理

### 📊 数据处理
- Excel 导入导出：支持大数据量处理
- 文件上传下载：MinIO 对象存储
- 数据字典：系统参数配置

### 📋 系统监控
- 操作日志：用户行为追踪
- 登录日志：安全审计
- 系统监控：性能指标

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 开源协议

本项目基于 [Apache License 2.0](LICENSE) 协议开源。

## 📞 联系方式

- 作者：IvanFan
- GitHub：[@IvanFan-sky](https://github.com/IvanFan-sky)

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Knife4j](https://doc.xiaominfo.com/)
- [Hutool](https://hutool.cn/)

---

⭐ 如果这个项目对你有帮助，请给它一个 Star！