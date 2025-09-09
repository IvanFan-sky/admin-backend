# Java后端项目单体多模块开发规范

## 1. 文档概述

### 1.1 文档目的
本文档旨在规范Admin管理系统Java后端项目的开发标准，包括代码规范、项目结构、开发流程、测试规范等，确保代码质量和团队协作效率。

### 1.2 适用范围
- Java后端开发人员
- 项目技术负责人
- 代码审查人员
- 新入职开发人员

### 1.3 规范原则
- **一致性**: 统一的代码风格和命名规范
- **可读性**: 清晰的代码结构和注释
- **可维护性**: 模块化设计和低耦合
- **可扩展性**: 支持功能扩展和技术升级
- **安全性**: 遵循安全编码规范

## 2. 项目结构规范

### 2.1 总体项目结构

```
admin-backend/
├── admin-dependencies/                    # 依赖管理模块
│   └── pom.xml
├── admin-framework/                       # 框架封装层
│   ├── admin-spring-boot-starter-web/     # Web框架封装
│   ├── admin-spring-boot-starter-security/# 安全框架封装
│   ├── admin-spring-boot-starter-mybatis/ # 数据访问封装
│   ├── admin-spring-boot-starter-redis/   # Redis封装
│   ├── admin-spring-boot-starter-mq/      # 消息队列封装
│   └── admin-common/                      # 通用工具类
├── admin-module-system/                   # 系统管理模块
│   ├── admin-module-system-api/           # API接口定义
│   └── admin-module-system-biz/           # 业务实现
├── admin-module-infra/                    # 基础设施模块
│   ├── admin-module-infra-api/
│   └── admin-module-infra-biz/
├── admin-server/                          # 启动服务模块
├── docs/                                  # 项目文档
├── sql/                                   # 数据库脚本
├── pom.xml                               # 根POM文件
└── README.md                             # 项目说明
```

### 2.2 模块命名规范

#### 2.2.1 模块命名格式
```
{project-name}-{layer}-{module}-{type}

示例:
- admin-framework-web          # 框架层Web模块
- admin-module-system-api      # 业务层系统模块API
- admin-module-system-biz      # 业务层系统模块实现
```

#### 2.2.2 层级说明
- **dependencies**: 依赖管理层
- **framework**: 框架封装层
- **module**: 业务模块层
- **server**: 启动服务层

#### 2.2.3 类型说明
- **api**: 接口定义模块
- **biz**: 业务实现模块
- **starter**: Spring Boot Starter模块

### 2.3 包结构规范

#### 2.3.1 API模块包结构
```
com.admin.module.{module}.api/
├── {business}/                    # 业务包
│   ├── dto/                      # 数据传输对象
│   │   ├── {Business}CreateReqDTO.java
│   │   ├── {Business}UpdateReqDTO.java
│   │   ├── {Business}RespDTO.java
│   │   └── {Business}PageReqDTO.java
│   ├── {Business}Api.java        # API接口
│   └── enums/                    # 枚举类
└── constant/                     # 常量类
```

#### 2.3.2 BIZ模块包结构
```
com.admin.module.{module}/
├── controller/                   # 控制器层
│   └── admin/                   # 管理端控制器
│       └── {business}/
├── service/                     # 服务层
│   └── {business}/
│       ├── {Business}Service.java
│       └── {Business}ServiceImpl.java
├── dal/                        # 数据访问层
│   ├── dataobject/            # 数据对象
│   │   └── {business}/
│   └── mysql/                 # MySQL数据访问
│       └── {business}/
├── convert/                   # 对象转换器
│   └── {business}/
├── job/                      # 定时任务
├── mq/                       # 消息队列
└── framework/                # 框架相关
    └── security/             # 安全配置
```

## 3. 代码规范

### 3.1 命名规范

#### 3.1.1 包命名
```java
// 正确示例
com.admin.module.system.controller.admin.user
com.admin.module.system.service.user
com.admin.module.system.dal.dataobject.user

// 错误示例
com.admin.module.system.Controller.Admin.User  // 大写
com.admin.module.system.service.userService    // 驼峰
```

#### 3.1.2 类命名
```java
// 控制器类
public class UserController {}
public class RoleController {}

// 服务类
public interface UserService {}
public class UserServiceImpl implements UserService {}

// 数据对象类
public class UserDO {}          // Data Object
public class UserCreateReqDTO {} // Request DTO
public class UserRespDTO {}      // Response DTO
public class UserPageReqVO {}    // View Object

// 工具类
public class StringUtils {}
public class DateUtils {}

// 常量类
public class UserConstants {}
public interface ErrorCodeConstants {}
```

#### 3.1.3 方法命名
```java
// 查询方法
public UserDO getUser(Long id) {}
public List<UserDO> getUserList(UserPageReqVO reqVO) {}
public PageResult<UserDO> getUserPage(UserPageReqVO reqVO) {}

// 创建方法
public Long createUser(UserCreateReqVO reqVO) {}

// 更新方法
public void updateUser(UserUpdateReqVO reqVO) {}

// 删除方法
public void deleteUser(Long id) {}

// 校验方法
public void validateUser(Long id) {}
public void validateUserExists(Long id) {}

// 转换方法
public UserRespVO convertToRespVO(UserDO user) {}
```

#### 3.1.4 变量命名
```java
// 常量命名 - 全大写，下划线分隔
public static final String DEFAULT_PASSWORD = "123456";
public static final int MAX_RETRY_COUNT = 3;

// 变量命名 - 驼峰命名
private String userName;
private List<UserDO> userList;
private Map<Long, String> userIdToNameMap;

// 布尔变量 - is/has/can开头
private boolean isEnabled;
private boolean hasPermission;
private boolean canDelete;
```

### 3.2 注释规范

#### 3.2.1 类注释
```java
/**
 * 用户管理服务实现类
 * 
 * 提供用户的增删改查、权限管理等核心功能
 * 支持用户状态管理、密码加密、角色分配等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
public class UserServiceImpl implements UserService {
    // 实现代码
}
```

#### 3.2.2 接口注释
```java
/**
 * 用户管理服务接口
 * 
 * 定义用户相关的业务操作规范
 * 包括用户生命周期管理、权限控制等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface UserService {
    // 接口方法定义
}
```

#### 3.2.3 方法注释
```java
/**
 * 创建用户
 * 
 * 1. 校验用户名唯一性
 * 2. 加密用户密码
 * 3. 分配默认角色
 * 4. 记录操作日志
 *
 * @param reqVO 创建用户请求参数，包含用户名、密码、邮箱等信息
 * @return 新创建的用户ID
 * @throws ServiceException 当用户名已存在或参数校验失败时抛出
 * @throws IllegalArgumentException 当必填参数为空时抛出
 * @author admin
 * @since 1.0
 */
public Long createUser(UserCreateReqVO reqVO) {
    // 实现代码
}

/**
 * 分页查询用户列表
 *
 * @param reqVO 查询条件，支持用户名、状态、创建时间范围等筛选
 * @return 分页结果，包含用户基本信息和角色信息
 */
public PageResult<UserRespVO> getUserPage(UserPageReqVO reqVO) {
    // 实现代码
}
```

#### 3.2.4 字段注释
```java
/**
 * 用户ID
 * 主键，自增长
 */
@TableId(type = IdType.AUTO)
private Long id;

/**
 * 用户名（登录账号）
 * 唯一索引，长度3-20位，支持字母数字下划线
 */
private String username;

/**
 * 用户状态
 * 1-正常 0-禁用 2-锁定
 * @see UserStatusEnum
 */
private Integer status;

/**
 * 最后登录时间
 * 用于统计用户活跃度和安全审计
 */
private LocalDateTime lastLoginTime;
```

#### 3.2.5 枚举注释
```java
/**
 * 用户状态枚举
 * 
 * 定义用户在系统中的各种状态
 * 用于权限控制和业务流程管理
 *
 * @author admin
 * @since 1.0
 */
public enum UserStatusEnum {
    
    /**
     * 正常状态
     * 用户可以正常登录和使用系统功能
     */
    NORMAL(1, "正常"),
    
    /**
     * 禁用状态
     * 管理员主动禁用，用户无法登录
     */
    DISABLED(0, "禁用"),
    
    /**
     * 锁定状态
     * 系统自动锁定，如密码错误次数过多
     */
    LOCKED(2, "锁定");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态描述
     */
    private final String desc;
}
```

#### 3.2.6 复杂业务逻辑注释
```java
public void processUserLogin(String username, String password) {
    // 1. 参数校验
    validateLoginParams(username, password);
    
    // 2. 查询用户信息
    UserDO user = getUserByUsername(username);
    if (user == null) {
        // 记录登录失败日志，防止用户名枚举攻击
        logLoginFailure(username, "用户不存在");
        throw new ServiceException(USER_NOT_EXISTS);
    }
    
    // 3. 检查用户状态
    if (!UserStatusEnum.NORMAL.getCode().equals(user.getStatus())) {
        throw new ServiceException(USER_STATUS_INVALID);
    }
    
    // 4. 验证密码
    if (!passwordEncoder.matches(password, user.getPassword())) {
        // 增加失败次数，达到阈值后锁定账户
        incrementLoginFailureCount(user.getId());
        throw new ServiceException(PASSWORD_INCORRECT);
    }
    
    // 5. 登录成功处理
    // 清除失败次数、更新登录时间、生成Token等
    handleLoginSuccess(user);
}
```

#### 3.2.7 注释规范要求

1. **必须添加注释的场景**：
   - 所有public类和接口
   - 所有public和protected方法
   - 复杂的private方法
   - 所有常量和枚举
   - 重要的字段属性
   - 复杂的业务逻辑代码块

2. **注释内容要求**：
   - 说明功能作用和业务含义
   - 描述重要的业务规则和约束
   - 标注参数的格式和取值范围
   - 说明返回值的含义和可能的状态
   - 列出可能抛出的异常及原因

3. **注释格式要求**：
   - 使用标准的JavaDoc格式
   - 中文注释，语言简洁明了
   - 及时更新，保持与代码同步
   - 避免无意义的注释（如：// 设置用户名 setUsername(name)）

### 3.3 代码格式规范

#### 3.3.1 缩进和空格
```java
// 使用4个空格缩进，不使用Tab
public class UserService {
    private UserMapper userMapper;
    
    public UserDO getUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userMapper.selectById(id);
    }
}
```

#### 3.3.2 行长度限制
```java
// 每行代码不超过120个字符
// 过长的方法调用应该换行
UserDO user = userService.createUser(
    username, 
    password, 
    email, 
    phone, 
    roleIds
);

// 过长的条件判断应该换行
if (StringUtils.hasText(username) 
    && StringUtils.hasText(password) 
    && CollectionUtils.isNotEmpty(roleIds)) {
    // 处理逻辑
}
```

#### 3.3.3 空行规范
```java
public class UserService {
    // 字段声明后空一行
    private UserMapper userMapper;
    
    // 方法之间空一行
    public UserDO getUser(Long id) {
        // 方法内逻辑块之间空一行
        validateUserId(id);
        
        UserDO user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(USER_NOT_EXISTS);
        }
        
        return user;
    }
    
    public void deleteUser(Long id) {
        // 实现代码
    }
}
```

## 4. 分层架构规范

### 4.1 Controller层规范

#### 4.1.1 Controller基本结构
```java
@RestController
@RequestMapping("/admin-api/system/users")
@Tag(name = "管理后台 - 用户管理")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(
            @Valid UserPageReqVO reqVO) {
        PageResult<UserDO> pageResult = userService.getUserPage(reqVO);
        return success(UserConvert.INSTANCE.convertPage(pageResult));
    }
    
    @PostMapping("/create")
    @Operation(summary = "创建用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateReqVO reqVO) {
        Long userId = userService.createUser(reqVO);
        return success(userId);
    }
}
```

#### 4.1.2 Controller规范要点
- 使用`@RestController`注解
- 统一使用`/admin-api`前缀
- 添加Swagger注解用于API文档生成
- 使用`@PreAuthorize`进行权限校验
- 参数校验使用`@Valid`注解
- 统一返回`CommonResult`格式

### 4.2 Service层规范

#### 4.2.1 Service接口定义
```java
/**
 * 用户管理服务
 *
 * @author admin
 */
public interface UserService {
    
    /**
     * 创建用户
     *
     * @param reqVO 创建用户请求
     * @return 用户ID
     */
    Long createUser(UserCreateReqVO reqVO);
    
    /**
     * 更新用户
     *
     * @param reqVO 更新用户请求
     */
    void updateUser(UserUpdateReqVO reqVO);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 获得用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDO getUser(Long id);
    
    /**
     * 获得用户分页列表
     *
     * @param reqVO 分页查询请求
     * @return 用户分页结果
     */
    PageResult<UserDO> getUserPage(UserPageReqVO reqVO);
}
```

#### 4.2.2 Service实现类
```java
@Service
@Validated
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Long createUser(UserCreateReqVO reqVO) {
        // 1. 校验用户名唯一性
        validateUserNameUnique(null, reqVO.getUsername());
        
        // 2. 校验邮箱唯一性
        validateEmailUnique(null, reqVO.getEmail());
        
        // 3. 校验手机号唯一性
        validatePhoneUnique(null, reqVO.getPhone());
        
        // 4. 创建用户
        UserDO user = UserConvert.INSTANCE.convert(reqVO);
        user.setPassword(passwordEncoder.encode(reqVO.getPassword()));
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        userMapper.insert(user);
        
        return user.getId();
    }
    
    @Override
    public void updateUser(UserUpdateReqVO reqVO) {
        // 1. 校验用户存在
        validateUserExists(reqVO.getId());
        
        // 2. 校验用户名唯一性
        validateUserNameUnique(reqVO.getId(), reqVO.getUsername());
        
        // 3. 更新用户
        UserDO updateObj = UserConvert.INSTANCE.convert(reqVO);
        userMapper.updateById(updateObj);
    }
    
    private void validateUserExists(Long id) {
        if (userMapper.selectById(id) == null) {
            throw exception(USER_NOT_EXISTS);
        }
    }
    
    private void validateUserNameUnique(Long id, String username) {
        UserDO user = userMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        if (id == null || !id.equals(user.getId())) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }
}
```

#### 4.2.3 Service规范要点
- 接口和实现类分离
- 使用`@Service`注解
- 添加`@Validated`注解支持参数校验
- 业务逻辑校验要完整
- 异常处理要规范
- 方法职责要单一

### 4.3 Mapper层规范

#### 4.3.1 Mapper接口定义
```java
/**
 * 用户数据访问层
 *
 * @author admin
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDO selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    UserDO selectByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    UserDO selectByPhone(@Param("phone") String phone);
    
    /**
     * 查询用户分页列表
     *
     * @param reqVO 查询条件
     * @return 用户列表
     */
    default PageResult<UserDO> selectPage(UserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(UserDO::getNickname, reqVO.getNickname())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserDO::getId));
    }
}
```

#### 4.3.2 XML映射文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.admin.module.system.dal.mysql.user.UserMapper">
    
    <!-- 根据用户名查询用户 -->
    <select id="selectByUsername" resultType="com.admin.module.system.dal.dataobject.user.UserDO">
        SELECT *
        FROM sys_user
        WHERE username = #{username}
          AND deleted = 0
    </select>
    
    <!-- 根据邮箱查询用户 -->
    <select id="selectByEmail" resultType="com.admin.module.system.dal.dataobject.user.UserDO">
        SELECT *
        FROM sys_user
        WHERE email = #{email}
          AND deleted = 0
    </select>
    
</mapper>
```

### 4.4 数据对象规范

#### 4.4.1 DO对象定义
```java
/**
 * 用户数据对象
 *
 * @author admin
 */
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDO extends BaseDO {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户邮箱
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 用户性别
     */
    private Integer sex;
    
    /**
     * 头像地址
     */
    private String avatar;
    
    /**
     * 帐号状态
     * 
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 最后登录IP
     */
    private String loginIp;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;
}
```

#### 4.4.2 DTO对象定义
```java
/**
 * 用户创建请求DTO
 *
 * @author admin
 */
@Data
@Schema(description = "用户创建请求")
public class UserCreateReqDTO {
    
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度为 4-30 个字符")
    private String username;
    
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;
    
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;
    
    @Schema(description = "用户邮箱", example = "admin@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;
    
    @Schema(description = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    @Schema(description = "用户性别", example = "1")
    @InEnum(SexEnum.class, message = "性别必须是 {value}")
    private Integer sex;
    
    @Schema(description = "角色ID列表", example = "[1, 2]")
    private Set<Long> roleIds;
}
```

## 5. 异常处理规范

### 5.1 异常体系设计

#### 5.1.1 异常类层次结构
```java
// 基础异常类
public abstract class BaseException extends RuntimeException {
    private final Integer code;
    private final String message;
    
    protected BaseException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }
}

// 业务异常类
public final class ServiceException extends BaseException {
    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ServiceException(ErrorCode errorCode, String... params) {
        super(errorCode);
        this.message = StrUtil.format(errorCode.getMsg(), params);
    }
}

// 系统异常类
public final class SystemException extends BaseException {
    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

#### 5.1.2 错误码定义
```java
/**
 * 错误码接口
 */
public interface ErrorCode {
    Integer getCode();
    String getMsg();
}

/**
 * 全局错误码枚举
 */
public enum GlobalErrorCodeConstants implements ErrorCode {
    
    SUCCESS(0, "成功"),
    
    // ========== 客户端错误段 ==========
    BAD_REQUEST(400, "请求参数不正确"),
    UNAUTHORIZED(401, "账号未登录"),
    FORBIDDEN(403, "没有该操作权限"),
    NOT_FOUND(404, "请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    LOCKED(423, "请求失败，请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
    
    // ========== 服务端错误段 ==========
    INTERNAL_SERVER_ERROR(500, "系统异常"),
    NOT_IMPLEMENTED(501, "功能未实现/未开启"),
    
    // ========== 自定义错误段 ==========
    REPEATED_REQUESTS(900, "重复请求，请稍后重试"),
    DEMO_DENY(901, "演示模式，禁止写操作");
    
    private final Integer code;
    private final String msg;
    
    GlobalErrorCodeConstants(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    @Override
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String getMsg() {
        return msg;
    }
}

/**
 * 用户模块错误码
 */
public interface UserErrorCodeConstants {
    
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1002001001, "用户账号已经存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1002001002, "用户邮箱已经存在");
    ErrorCode USER_PHONE_EXISTS = new ErrorCode(1002001003, "用户手机号已经存在");
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1002001004, "用户不存在");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode(1002001005, "用户密码校验失败");
    ErrorCode USER_IS_DISABLE = new ErrorCode(1002001006, "用户被禁用");
}
```

### 5.2 异常处理机制

#### 5.2.1 全局异常处理器
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常 ServiceException
     */
    @ExceptionHandler(ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        log.info("[serviceExceptionHandler]", ex);
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public CommonResult<?> systemExceptionHandler(SystemException ex) {
        log.error("[systemExceptionHandler]", ex);
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        assert fieldError != null;
        return CommonResult.error(BAD_REQUEST.getCode(), 
            String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }
    
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][用户({}) 访问 url({}) 时，没有权限]", 
            getLoginUserId(), getRequest().getRequestURI(), ex);
        return CommonResult.error(FORBIDDEN);
    }
    
    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<?> defaultExceptionHandler(Exception ex) {
        log.error("[defaultExceptionHandler]", ex);
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }
}
```

#### 5.2.2 异常使用规范
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public Long createUser(UserCreateReqVO reqVO) {
        // 校验用户名唯一性
        if (userMapper.selectByUsername(reqVO.getUsername()) != null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        
        // 校验邮箱唯一性
        if (StrUtil.isNotBlank(reqVO.getEmail()) 
            && userMapper.selectByEmail(reqVO.getEmail()) != null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        
        // 创建用户逻辑...
        return userId;
    }
    
    /**
     * 抛出业务异常的便捷方法
     */
    private ServiceException exception(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }
    
    private ServiceException exception(ErrorCode errorCode, String... params) {
        return new ServiceException(errorCode, params);
    }
}
```

## 6. 数据访问规范

### 6.1 MyBatis-Plus使用规范

**依赖配置**：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

#### 6.1.1 基础配置
```java
@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        // 数据权限插件
        interceptor.addInnerInterceptor(new DataPermissionInterceptor());
        
        return interceptor;
    }
    
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }
}
```

#### 6.1.2 基础实体类
```java
/**
 * 基础实体对象
 */
@Data
public abstract class BaseDO {
    
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
```

#### 6.1.3 自动填充处理器
```java
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) metaObject.getOriginalObject();
            
            LocalDateTime current = LocalDateTime.now();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }
            
            Long userId = getLoginUserId();
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreateBy())) {
                baseDO.setCreateBy(userId.toString());
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdateBy())) {
                baseDO.setUpdateBy(userId.toString());
            }
        }
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间为空，则以当前时间为更新时间
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
        
        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Object modifier = getFieldValByName("updateBy", metaObject);
        Long userId = getLoginUserId();
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updateBy", userId.toString(), metaObject);
        }
    }
}
```

### 6.2 MapStruct对象转换规范

**依赖配置**：
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

#### 6.2.1 基础转换器定义
```java
/**
 * 用户转换器
 */
@Mapper(componentModel = "spring")
public interface UserConvert {
    
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);
    
    /**
     * DO转换为VO
     */
    UserRespVO convert(UserDO bean);
    
    /**
     * DO列表转换为VO列表
     */
    List<UserRespVO> convertList(List<UserDO> list);
    
    /**
     * 分页结果转换
     */
    PageResult<UserRespVO> convertPage(PageResult<UserDO> page);
    
    /**
     * 创建请求转换为DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    UserDO convert(UserSaveReqVO bean);
}
```

#### 6.2.2 复杂映射规则
```java
@Mapper(componentModel = "spring")
public interface OrderConvert {
    
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);
    
    /**
     * 自定义字段映射
     */
    @Mapping(source = "user.nickname", target = "userName")
    @Mapping(source = "createTime", target = "createTimeStr", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "statusName", expression = "java(getStatusName(bean.getStatus()))")
    OrderRespVO convert(OrderDO bean);
    
    /**
     * 状态转换方法
     */
    default String getStatusName(Integer status) {
        return OrderStatusEnum.valueOf(status).getName();
    }
}
```

#### 6.2.3 使用规范
1. **统一命名**: 转换器类名以Convert结尾
2. **单例模式**: 使用INSTANCE静态实例
3. **忽略字段**: 使用@Mapping(target = "field", ignore = true)
4. **自定义转换**: 使用expression或qualifiedByName
5. **日期格式**: 使用dateFormat指定日期格式

### 6.3 查询构造器使用规范

#### 6.3.1 Lambda查询构造器
```java
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    
    /**
     * 查询用户分页列表
     */
    default PageResult<UserDO> selectPage(UserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(UserDO::getNickname, reqVO.getNickname())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(UserDO::getSex, reqVO.getSex())
                .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserDO::getId));
    }
    
    /**
     * 查询指定角色的用户列表
     */
    default List<UserDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<UserDO>()
                .eq(UserDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .apply("FIND_IN_SET({0}, role_ids)", roleId));
    }
}
```

#### 6.3.2 复杂查询使用XML
```xml
<mapper namespace="com.admin.module.system.dal.mysql.user.UserMapper">
    
    <!-- 根据条件查询用户统计信息 -->
    <select id="selectUserStatistics" resultType="com.admin.module.system.dal.dataobject.user.UserStatisticsDO">
        SELECT 
            COUNT(*) as totalCount,
            COUNT(CASE WHEN status = 1 THEN 1 END) as enableCount,
            COUNT(CASE WHEN status = 0 THEN 1 END) as disableCount,
            COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) as todayCount
        FROM sys_user
        WHERE deleted = 0
        <if test="createTime != null">
            AND create_time >= #{createTime[0]} AND create_time <= #{createTime[1]}
        </if>
    </select>
    
    <!-- 批量更新用户状态 -->
    <update id="updateStatusBatch">
        UPDATE sys_user 
        SET status = #{status}, update_time = NOW()
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        AND deleted = 0
    </update>
    
</mapper>
```

## 7. 缓存使用规范

### 7.1 Redis缓存规范

#### 7.1.1 缓存Key命名规范
```java
/**
 * 缓存Key常量类
 */
public interface CacheKeyConstants {
    
    /**
     * 用户缓存Key前缀
     */
    String USER_PREFIX = "user:";
    
    /**
     * 用户信息缓存Key
     * user:info:{userId}
     */
    String USER_INFO = USER_PREFIX + "info:%s";
    
    /**
     * 用户权限缓存Key
     * user:permission:{userId}
     */
    String USER_PERMISSION = USER_PREFIX + "permission:%s";
    
    /**
     * 角色缓存Key前缀
     */
    String ROLE_PREFIX = "role:";
    
    /**
     * 角色信息缓存Key
     * role:info:{roleId}
     */
    String ROLE_INFO = ROLE_PREFIX + "info:%s";
    
    /**
     * 菜单缓存Key前缀
     */
    String MENU_PREFIX = "menu:";
    
    /**
     * 菜单树缓存Key
     */
    String MENU_TREE = MENU_PREFIX + "tree";
}
```

#### 7.1.2 缓存服务封装
```java
@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 设置缓存
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    
    /**
     * 获取缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null : (T) value;
    }
    
    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * 批量删除缓存
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }
    
    /**
     * 设置Hash缓存
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
    
    /**
     * 获取Hash缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey, Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        return value == null ? null : (T) value;
    }
}
```

#### 7.1.3 缓存使用示例
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CacheService cacheService;
    
    @Override
    public UserDO getUser(Long id) {
        // 先从缓存获取
        String cacheKey = String.format(USER_INFO, id);
        UserDO user = cacheService.get(cacheKey, UserDO.class);
        if (user != null) {
            return user;
        }
        
        // 缓存不存在，从数据库查询
        user = userMapper.selectById(id);
        if (user != null) {
            // 设置缓存，过期时间30分钟
            cacheService.set(cacheKey, user, Duration.ofMinutes(30));
        }
        
        return user;
    }
    
    @Override
    public void updateUser(UserUpdateReqVO reqVO) {
        // 更新数据库
        UserDO updateObj = UserConvert.INSTANCE.convert(reqVO);
        userMapper.updateById(updateObj);
        
        // 删除缓存
        String cacheKey = String.format(USER_INFO, reqVO.getId());
        cacheService.delete(cacheKey);
        
        // 删除相关权限缓存
        String permissionKey = String.format(USER_PERMISSION, reqVO.getId());
        cacheService.delete(permissionKey);
    }
}
```

### 7.2 本地缓存规范

#### 7.2.1 Caffeine缓存配置
```java
@Configuration
public class CaffeineConfig {
    
    @Bean
    public CacheManager localCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        return cacheManager;
    }
}
```

#### 7.2.2 本地缓存使用
```java
@Service
public class DictDataService {
    
    @Cacheable(value = "dictData", key = "#dictType", cacheManager = "localCacheManager")
    public List<DictDataDO> getDictDataList(String dictType) {
        return dictDataMapper.selectListByDictType(dictType);
    }
    
    @CacheEvict(value = "dictData", key = "#dictType", cacheManager = "localCacheManager")
    public void refreshDictDataCache(String dictType) {
        // 刷新缓存
    }
}
```

## 8. 日志规范

### 8.1 日志级别使用规范

```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Override
    public Long createUser(UserCreateReqVO reqVO) {
        // DEBUG: 调试信息，开发环境使用
        log.debug("开始创建用户，参数: {}", reqVO);
        
        try {
            // 业务逻辑处理
            validateUserData(reqVO);
            
            UserDO user = UserConvert.INSTANCE.convert(reqVO);
            userMapper.insert(user);
            
            // INFO: 重要业务操作记录
            log.info("用户创建成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            
            return user.getId();
            
        } catch (ServiceException e) {
            // WARN: 业务异常，预期内的错误
            log.warn("用户创建失败，原因: {}, 参数: {}", e.getMessage(), reqVO);
            throw e;
            
        } catch (Exception e) {
            // ERROR: 系统异常，非预期错误
            log.error("用户创建异常，参数: {}", reqVO, e);
            throw new SystemException(INTERNAL_SERVER_ERROR);
        }
    }
    
    private void validateUserData(UserCreateReqVO reqVO) {
        // TRACE: 最详细的调试信息
        log.trace("校验用户数据: {}", reqVO);
        
        if (userMapper.selectByUsername(reqVO.getUsername()) != null) {
            log.warn("用户名已存在: {}", reqVO.getUsername());
            throw exception(USER_USERNAME_EXISTS);
        }
    }
}
```

### 8.2 操作日志规范

#### 8.2.1 操作日志注解
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作名称
     */
    String name() default "";
    
    /**
     * 操作类型
     */
    OperateTypeEnum type() default OperateTypeEnum.OTHER;
    
    /**
     * 是否记录操作日志
     */
    boolean enable() default true;
}
```

#### 8.2.2 操作日志使用
```java
@RestController
@RequestMapping("/admin-api/system/users")
public class UserController {
    
    @PostMapping("/create")
    @OperateLog(module = "系统管理", name = "创建用户", type = OperateTypeEnum.CREATE)
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateReqVO reqVO) {
        Long userId = userService.createUser(reqVO);
        return success(userId);
    }
    
    @PutMapping("/update")
    @OperateLog(module = "系统管理", name = "更新用户", type = OperateTypeEnum.UPDATE)
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }
    
    @DeleteMapping("/delete")
    @OperateLog(module = "系统管理", name = "删除用户", type = OperateTypeEnum.DELETE)
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }
}
```

## 9. 测试规范

### 9.1 单元测试规范

#### 9.1.1 测试类命名和结构
```java
/**
 * 用户服务单元测试
 * 
 * 测试类命名: {被测试类名}Test
 * 测试方法命名: test{方法名}_{测试场景}
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void testCreateUser_Success() {
        // Given - 准备测试数据
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setUsername("testuser");
        reqVO.setPassword("123456");
        reqVO.setEmail("test@example.com");
        
        when(userMapper.selectByUsername("testuser")).thenReturn(null);
        when(userMapper.selectByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("encoded_password");
        when(userMapper.insert(any(UserDO.class))).thenAnswer(invocation -> {
            UserDO user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });
        
        // When - 执行测试方法
        Long userId = userService.createUser(reqVO);
        
        // Then - 验证结果
        assertThat(userId).isEqualTo(1L);
        verify(userMapper).selectByUsername("testuser");
        verify(userMapper).selectByEmail("test@example.com");
        verify(passwordEncoder).encode("123456");
        verify(userMapper).insert(any(UserDO.class));
    }
    
    @Test
    void testCreateUser_UsernameExists() {
        // Given
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setUsername("existuser");
        
        UserDO existUser = new UserDO();
        existUser.setId(1L);
        existUser.setUsername("existuser");
        when(userMapper.selectByUsername("existuser")).thenReturn(existUser);
        
        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, 
            () -> userService.createUser(reqVO));
        assertThat(exception.getCode()).isEqualTo(USER_USERNAME_EXISTS.getCode());
    }
}
```

#### 9.1.2 Controller测试规范
```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setUsername("testuser");
        reqVO.setPassword("123456");
        
        when(userService.createUser(any(UserCreateReqVO.class))).thenReturn(1L);
        
        // When & Then
        mockMvc.perform(post("/admin-api/system/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(1))
                .andExpect(jsonPath("$.msg").value("success"));
        
        verify(userService).createUser(any(UserCreateReqVO.class));
    }
    
    @Test
    void testCreateUser_ValidationFailed() throws Exception {
        // Given - 用户名为空的请求
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setPassword("123456");
        
        // When & Then
        mockMvc.perform(post("/admin-api/system/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJsonString(reqVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value(containsString("用户名不能为空")));
    }
}
```

### 9.2 集成测试规范

#### 9.2.1 数据库集成测试
```java
@SpringBootTest
@Transactional
@Rollback
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    void testUserCRUDOperations() {
        // Create - 创建用户
        UserCreateReqVO createReq = new UserCreateReqVO();
        createReq.setUsername("integration_test_user");
        createReq.setPassword("123456");
        createReq.setEmail("integration@test.com");
        createReq.setNickname("集成测试用户");
        
        Long userId = userService.createUser(createReq);
        assertThat(userId).isNotNull();
        
        // Read - 查询用户
        UserDO user = userService.getUser(userId);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("integration_test_user");
        assertThat(user.getEmail()).isEqualTo("integration@test.com");
        
        // Update - 更新用户
        UserUpdateReqVO updateReq = new UserUpdateReqVO();
        updateReq.setId(userId);
        updateReq.setNickname("更新后的昵称");
        updateReq.setPhone("13800138000");
        
        userService.updateUser(updateReq);
        
        // 验证更新结果
        UserDO updatedUser = userService.getUser(userId);
        assertThat(updatedUser.getNickname()).isEqualTo("更新后的昵称");
        assertThat(updatedUser.getPhone()).isEqualTo("13800138000");
        
        // Delete - 删除用户
        userService.deleteUser(userId);
        
        // 验证删除结果（逻辑删除）
        UserDO deletedUser = userMapper.selectById(userId);
        assertThat(deletedUser.getDeleted()).isTrue();
    }
    
    @Test
    void testUserPageQuery() {
        // 准备测试数据
        createTestUsers();
        
        // 测试分页查询
        UserPageReqVO pageReq = new UserPageReqVO();
        pageReq.setPageNo(1);
        pageReq.setPageSize(10);
        pageReq.setUsername("test");
        
        PageResult<UserDO> pageResult = userService.getUserPage(pageReq);
        
        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getList()).isNotEmpty();
        assertThat(pageResult.getTotal()).isGreaterThan(0);
    }
    
    private void createTestUsers() {
        for (int i = 1; i <= 5; i++) {
            UserCreateReqVO reqVO = new UserCreateReqVO();
            reqVO.setUsername("test_user_" + i);
            reqVO.setPassword("123456");
            reqVO.setEmail("test" + i + "@example.com");
            reqVO.setNickname("测试用户" + i);
            userService.createUser(reqVO);
        }
    }
}
```

## 10. 性能优化规范

### 10.1 数据库性能优化

#### 10.1.1 SQL优化规范
```java
// 好的实践：使用索引字段查询
@Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
UserDO selectByUsername(@Param("username") String username);

// 避免：全表扫描
@Select("SELECT * FROM sys_user WHERE nickname LIKE CONCAT('%', #{nickname}, '%')")
List<UserDO> selectByNicknameLike(@Param("nickname") String nickname);

// 好的实践：分页查询限制返回数量
default PageResult<UserDO> selectPage(UserPageReqVO reqVO) {
    return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
            .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
            .orderByDesc(UserDO::getId));
}

// 避免：一次性查询大量数据
List<UserDO> selectAll();
```

#### 10.1.2 批量操作优化
```java
// 好的实践：批量插入
@Service
public class UserServiceImpl {
    
    public void batchCreateUsers(List<UserCreateReqVO> reqVOList) {
        if (CollectionUtils.isEmpty(reqVOList)) {
            return;
        }
        
        // 分批处理，每批1000条
        int batchSize = 1000;
        for (int i = 0; i < reqVOList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, reqVOList.size());
            List<UserCreateReqVO> batch = reqVOList.subList(i, endIndex);
            
            List<UserDO> users = UserConvert.INSTANCE.convertList(batch);
            userMapper.insertBatch(users);
        }
    }
}

// Mapper中的批量插入
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    
    /**
     * 批量插入用户
     */
    @Insert("<script>" +
            "INSERT INTO sys_user (username, password, nickname, email, phone, status, create_time) VALUES " +
            "<foreach collection='users' item='user' separator=','>" +
            "(#{user.username}, #{user.password}, #{user.nickname}, #{user.email}, #{user.phone}, #{user.status}, #{user.createTime})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("users") List<UserDO> users);
}
```

### 10.2 缓存性能优化

#### 10.2.1 缓存策略
```java
@Service
public class UserServiceImpl {
    
    // 缓存穿透防护
    public UserDO getUser(Long id) {
        String cacheKey = String.format(USER_INFO, id);
        
        // 先查缓存
        UserDO user = cacheService.get(cacheKey, UserDO.class);
        if (user != null) {
            return user;
        }
        
        // 使用分布式锁防止缓存击穿
        String lockKey = "lock:user:" + id;
        try (RedisLock lock = redisLockService.lock(lockKey, Duration.ofSeconds(10))) {
            // 双重检查
            user = cacheService.get(cacheKey, UserDO.class);
            if (user != null) {
                return user;
            }
            
            // 查询数据库
            user = userMapper.selectById(id);
            
            if (user != null) {
                // 正常数据缓存30分钟
                cacheService.set(cacheKey, user, Duration.ofMinutes(30));
            } else {
                // 空值缓存5分钟，防止缓存穿透
                cacheService.set(cacheKey, new UserDO(), Duration.ofMinutes(5));
            }
            
            return user;
        }
    }
    
    // 缓存预热
    @PostConstruct
    public void warmUpCache() {
        // 预热热点数据
        List<UserDO> hotUsers = userMapper.selectHotUsers();
        for (UserDO user : hotUsers) {
            String cacheKey = String.format(USER_INFO, user.getId());
            cacheService.set(cacheKey, user, Duration.ofMinutes(30));
        }
    }
}
```

#### 10.2.2 缓存更新策略
```java
@Service
public class UserServiceImpl {
    
    @Override
    @Transactional
    public void updateUser(UserUpdateReqVO reqVO) {
        // 更新数据库
        UserDO updateObj = UserConvert.INSTANCE.convert(reqVO);
        userMapper.updateById(updateObj);
        
        // 延迟双删策略
        String cacheKey = String.format(USER_INFO, reqVO.getId());
        
        // 第一次删除
        cacheService.delete(cacheKey);
        
        // 异步延迟删除
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000); // 延迟1秒
                cacheService.delete(cacheKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

### 10.3 异步处理优化

#### 10.3.1 异步任务配置
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("async-task-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.initialize();
        return executor;
    }
}
```

#### 10.3.2 异步方法使用
```java
@Service
public class UserServiceImpl {
    
    @Async("taskExecutor")
    public CompletableFuture<Void> sendWelcomeEmail(Long userId) {
        try {
            UserDO user = userMapper.selectById(userId);
            if (user != null && StrUtil.isNotBlank(user.getEmail())) {
                emailService.sendWelcomeEmail(user.getEmail(), user.getNickname());
            }
        } catch (Exception e) {
            log.error("发送欢迎邮件失败，用户ID: {}", userId, e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public Long createUser(UserCreateReqVO reqVO) {
        // 同步创建用户
        UserDO user = UserConvert.INSTANCE.convert(reqVO);
        userMapper.insert(user);
        
        // 异步发送欢迎邮件
        sendWelcomeEmail(user.getId());
        
        return user.getId();
    }
}
```

## 11. 安全规范

### 11.1 输入验证规范

#### 11.1.1 参数校验
```java
@Data
@Schema(description = "用户创建请求")
public class UserCreateReqDTO {
    
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度为 4-30 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为 6-20 位")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$", 
             message = "密码必须包含大小写字母和数字")
    private String password;
    
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;
    
    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
}
```

#### 11.1.2 SQL注入防护
```java
// 好的实践：使用参数化查询
@Select("SELECT * FROM sys_user WHERE username = #{username} AND status = #{status}")
List<UserDO> selectByUsernameAndStatus(@Param("username") String username, 
                                       @Param("status") Integer status);

// 动态SQL使用MyBatis-Plus构造器
default List<UserDO> selectByCondition(UserQueryReqVO reqVO) {
    return selectList(new LambdaQueryWrapperX<UserDO>()
            .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
            .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
            .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime()));
}

// 避免：字符串拼接SQL
@Select("SELECT * FROM sys_user WHERE username = '${username}'")
List<UserDO> selectByUsernameBad(@Param("username") String username);
```

### 11.2 权限控制规范

#### 11.2.1 接口权限控制
```java
@RestController
@RequestMapping("/admin-api/system/users")
public class UserController {
    
    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO reqVO) {
        // 实现代码
    }
    
    @PostMapping("/create")
    @Operation(summary = "创建用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateReqVO reqVO) {
        // 实现代码
    }
    
    @PutMapping("/update")
    @Operation(summary = "更新用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        // 实现代码
    }
    
    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        // 实现代码
    }
}
```

#### 11.2.2 数据权限控制
```java
@Service
public class UserServiceImpl {
    
    @Override
    public PageResult<UserDO> getUserPage(UserPageReqVO reqVO) {
        // 获取当前用户的数据权限
        DataPermissionContext context = DataPermissionContextHolder.get();
        
        return userMapper.selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                // 应用数据权限过滤
                .apply(context.getSqlCondition())
                .orderByDesc(UserDO::getId));
    }
}
```

### 11.3 敏感数据处理

#### 11.3.1 密码加密
```java
@Service
public class UserServiceImpl {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Long createUser(UserCreateReqVO reqVO) {
        // 密码加密
        String encodedPassword = passwordEncoder.encode(reqVO.getPassword());
        
        UserDO user = UserConvert.INSTANCE.convert(reqVO);
        user.setPassword(encodedPassword);
        
        userMapper.insert(user);
        return user.getId();
    }
    
    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

#### 11.3.2 敏感信息脱敏
```java
@Data
public class UserRespVO {
    
    private Long id;
    private String username;
    private String nickname;
    
    @JsonSerialize(using = EmailDesensitizeSerializer.class)
    private String email;
    
    @JsonSerialize(using = PhoneDesensitizeSerializer.class)
    private String phone;
    
    // 密码字段不返回给前端
    @JsonIgnore
    private String password;
}

// 邮箱脱敏序列化器
public class EmailDesensitizeSerializer extends JsonSerializer<String> {
    
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            gen.writeString(value);
            return;
        }
        
        String prefix = value.substring(0, 1);
        String suffix = value.substring(atIndex);
        String masked = prefix + "***" + suffix;
        gen.writeString(masked);
    }
}
```

## 12. 部署规范

### 12.1 Docker化部署

#### 12.1.1 Dockerfile
```dockerfile
# 使用官方OpenJDK 17镜像作为基础镜像
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 创建应用用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制应用JAR文件
COPY target/admin-server-*.jar app.jar

# 设置文件权限
RUN chown appuser:appuser app.jar

# 切换到应用用户
USER appuser

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+PrintGCDetails"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### 12.1.2 docker-compose.yml
```yaml
version: '3.8'

services:
  admin-backend:
    build: .
    container_name: admin-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/admin_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      - SPRING_DATASOURCE_USERNAME=admin
      - SPRING_DATASOURCE_PASSWORD=admin123
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
    depends_on:
      - mysql
      - redis
    networks:
      - admin-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mysql:
    image: mysql:8.0
    container_name: admin-mysql
    environment:
      - MYSQL_ROOT_PASSWORD=root123
      - MYSQL_DATABASE=admin_db
      - MYSQL_USER=admin
      - MYSQL_PASSWORD=admin123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - admin-network
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    container_name: admin-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - admin-network
    restart: unless-stopped
    command: redis-server --appendonly yes

volumes:
  mysql_data:
  redis_data:

networks:
  admin-network:
    driver: bridge
```

### 12.2 配置管理

#### 12.2.1 多环境配置
```yaml
# application.yml - 基础配置
spring:
  application:
    name: admin-backend
  profiles:
    active: @spring.profiles.active@
  
# application-dev.yml - 开发环境
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  redis:
    host: localhost
    port: 6379
    database: 0

logging:
  level:
    com.admin: DEBUG
    org.springframework.web: DEBUG

# application-prod.yml - 生产环境
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  redis:
    host: ${SPRING_REDIS_HOST}
    port: ${SPRING_REDIS_PORT}
    password: ${SPRING_REDIS_PASSWORD:}

logging:
  level:
    root: INFO
    com.admin: INFO
  file:
    name: /app/logs/admin-backend.log
```

## 13. 监控和运维规范

### 13.1 应用监控

#### 13.1.1 Actuator配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
  info:
    git:
      mode: full
```

#### 13.1.2 自定义健康检查
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                        .withDetail("database", "Available")
                        .withDetail("validationQuery", "SELECT 1")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withException(e)
                    .build();
        }
        
        return Health.down()
                .withDetail("database", "Connection validation failed")
                .build();
    }
}
```

### 13.2 日志管理

#### 13.2.1 Logback配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/app/logs/admin-backend.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/app/logs/admin-backend.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 错误日志单独输出 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <file>/app/logs/admin-backend-error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/app/logs/admin-backend-error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 开发环境 -->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.admin" level="DEBUG"/>
    </springProfile>
    
    <!-- 生产环境 -->
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>
    
</configuration>
```

## 14. 总结

本开发规范涵盖了Java后端项目单体多模块开发的各个方面，包括：

1. **项目结构规范**: 统一的模块命名和包结构
2. **代码规范**: 命名、注释、格式等编码标准
3. **分层架构规范**: Controller、Service、Mapper各层的职责和实现规范
4. **异常处理规范**: 统一的异常体系和处理机制
5. **数据访问规范**: MyBatis-Plus的使用规范和最佳实践
6. **缓存使用规范**: Redis和本地缓存的使用策略
7. **日志规范**: 日志级别和操作日志的使用规范
8. **测试规范**: 单元测试和集成测试的编写规范
9. **性能优化规范**: 数据库、缓存、异步处理的优化策略
10. **安全规范**: 输入验证、权限控制、敏感数据处理
11. **部署规范**: Docker化部署和配置管理
12. **监控运维规范**: 应用监控和日志管理

遵循这些规范可以确保项目的代码质量、可维护性和团队协作效率。建议团队成员认真学习并在日常开发中严格执行这些规范。