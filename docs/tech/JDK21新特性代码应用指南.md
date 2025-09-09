# JDK21新特性在Admin后端项目中的应用指南

## 1. 概述

本项目使用JDK21，应充分利用其新特性来提升代码质量、开发效率和可维护性。本指南详细说明了JDK21新特性在实际业务代码中的应用场景和最佳实践。

## 2. Record类的应用

### 2.1 DTO对象定义

**传统写法 vs Record写法对比**：

```java
// 传统DTO写法（不推荐）
public class UserCreateReqDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private Set<Long> roleIds;
    
    // 构造方法、getter、setter、equals、hashCode、toString...
    // 大量样板代码
}

// JDK21 Record写法（推荐）
public record UserCreateReqDTO(
    @NotBlank(message = "用户名不能为空")
    String username,
    
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    String password,
    
    @Email(message = "邮箱格式不正确")
    String email,
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    String phone,
    
    @NotEmpty(message = "角色不能为空")
    Set<Long> roleIds
) {
    // 可以添加自定义验证方法
    public UserCreateReqDTO {
        if (roleIds != null && roleIds.size() > 10) {
            throw new IllegalArgumentException("角色数量不能超过10个");
        }
    }
    
    // 可以添加业务方法
    public boolean isAdmin() {
        return roleIds != null && roleIds.contains(1L);
    }
}
```

### 2.2 API响应对象

```java
// 通用API响应
public record CommonResult<T>(
    Integer code,
    String message,
    T data,
    Long timestamp
) {
    // 成功响应的静态工厂方法
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data, System.currentTimeMillis());
    }
    
    // 失败响应的静态工厂方法
    public static <T> CommonResult<T> error(Integer code, String message) {
        return new CommonResult<>(code, message, null, System.currentTimeMillis());
    }
}

// 分页响应对象
public record PageResult<T>(
    List<T> list,
    Long total,
    Integer pageNo,
    Integer pageSize
) {
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 10);
    }
    
    public boolean hasNext() {
        return (long) pageNo * pageSize < total;
    }
}
```

### 2.3 配置类定义

```java
// 数据库配置
public record DatabaseConfig(
    String url,
    String username,
    String password,
    Integer maxPoolSize,
    Integer minPoolSize,
    Long connectionTimeout
) {
    // 紧凑构造器进行参数验证
    public DatabaseConfig {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("最大连接池大小必须大于0");
        }
        if (minPoolSize < 0) {
            throw new IllegalArgumentException("最小连接池大小不能小于0");
        }
        if (minPoolSize > maxPoolSize) {
            throw new IllegalArgumentException("最小连接池大小不能大于最大连接池大小");
        }
    }
}

// Redis配置
public record RedisConfig(
    String host,
    Integer port,
    String password,
    Integer database,
    Duration timeout
) {
    public RedisConfig {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("端口号必须在1-65535之间");
        }
    }
}
```

### 2.4 事件对象定义

```java
// 用户注册事件
public record UserRegisteredEvent(
    Long userId,
    String username,
    String email,
    LocalDateTime registeredAt
) implements ApplicationEvent {
    
    public static UserRegisteredEvent of(UserDO user) {
        return new UserRegisteredEvent(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreateTime()
        );
    }
}

// 订单状态变更事件
public record OrderStatusChangedEvent(
    Long orderId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    String reason,
    LocalDateTime changedAt
) {
    public boolean isCompleted() {
        return newStatus == OrderStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return newStatus == OrderStatus.CANCELLED;
    }
}
```

## 3. Switch表达式的应用

### 3.1 状态处理

```java
@Service
public class OrderService {
    
    // 传统switch语句（不推荐）
    public String getStatusMessageOld(OrderStatus status) {
        String message;
        switch (status) {
            case PENDING:
                message = "订单待支付";
                break;
            case PAID:
                message = "订单已支付";
                break;
            case SHIPPED:
                message = "订单已发货";
                break;
            case DELIVERED:
                message = "订单已送达";
                break;
            case CANCELLED:
                message = "订单已取消";
                break;
            default:
                message = "未知状态";
        }
        return message;
    }
    
    // JDK21 Switch表达式（推荐）
    public String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "订单待支付";
            case PAID -> "订单已支付";
            case SHIPPED -> "订单已发货";
            case DELIVERED -> "订单已送达";
            case CANCELLED -> "订单已取消";
        };
    }
    
    // 复杂业务逻辑处理
    public OrderAction getNextAction(OrderStatus status, PaymentStatus paymentStatus) {
        return switch (status) {
            case PENDING -> switch (paymentStatus) {
                case UNPAID -> OrderAction.PAY;
                case PAID -> OrderAction.CONFIRM;
                case FAILED -> OrderAction.RETRY_PAYMENT;
            };
            case PAID -> OrderAction.SHIP;
            case SHIPPED -> OrderAction.TRACK;
            case DELIVERED -> OrderAction.EVALUATE;
            case CANCELLED -> OrderAction.REFUND;
        };
    }
    
    // 计算订单手续费
    public BigDecimal calculateFee(OrderType orderType, BigDecimal amount) {
        return switch (orderType) {
            case NORMAL -> amount.multiply(new BigDecimal("0.01")); // 1%
            case VIP -> amount.multiply(new BigDecimal("0.005")); // 0.5%
            case ENTERPRISE -> BigDecimal.ZERO; // 免手续费
            case URGENT -> amount.multiply(new BigDecimal("0.02")); // 2%
        };
    }
}
```

### 3.2 类型转换处理

```java
@Component
public class DataConverter {
    
    public Object convertValue(String type, String value) {
        return switch (type.toLowerCase()) {
            case "int", "integer" -> Integer.parseInt(value);
            case "long" -> Long.parseLong(value);
            case "double" -> Double.parseDouble(value);
            case "float" -> Float.parseFloat(value);
            case "boolean", "bool" -> Boolean.parseBoolean(value);
            case "date" -> LocalDate.parse(value);
            case "datetime" -> LocalDateTime.parse(value);
            case "bigdecimal", "decimal" -> new BigDecimal(value);
            default -> value; // 默认返回字符串
        };
    }
    
    // HTTP状态码处理
    public String getHttpStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 -> "请求成功";
            case 201 -> "创建成功";
            case 400 -> "请求参数错误";
            case 401 -> "未授权访问";
            case 403 -> "权限不足";
            case 404 -> "资源不存在";
            case 500 -> "服务器内部错误";
            case 502 -> "网关错误";
            case 503 -> "服务不可用";
            default -> "未知状态码: " + statusCode;
        };
    }
}
```

### 3.3 权限验证

```java
@Component
public class PermissionChecker {
    
    public boolean hasPermission(UserRole role, String resource, String action) {
        return switch (role) {
            case ADMIN -> true; // 管理员拥有所有权限
            case MANAGER -> switch (resource) {
                case "user", "role", "menu" -> true;
                case "system" -> "read".equals(action);
                default -> false;
            };
            case USER -> switch (resource) {
                case "profile" -> true;
                case "order" -> "read".equals(action) || "create".equals(action);
                default -> false;
            };
            case GUEST -> "read".equals(action) && "public".equals(resource);
        };
    }
}
```

## 4. 文本块(Text Blocks)的应用

### 4.1 SQL语句定义

```java
@Repository
public class UserRepository {
    
    // 复杂查询SQL
    private static final String COMPLEX_USER_QUERY = """
        SELECT 
            u.id,
            u.username,
            u.nickname,
            u.email,
            u.phone,
            u.status,
            u.create_time,
            GROUP_CONCAT(r.name) as role_names,
            d.name as dept_name
        FROM sys_user u
        LEFT JOIN sys_user_role ur ON u.id = ur.user_id
        LEFT JOIN sys_role r ON ur.role_id = r.id
        LEFT JOIN sys_dept d ON u.dept_id = d.id
        WHERE u.deleted = 0
        AND u.status = ?
        GROUP BY u.id
        ORDER BY u.create_time DESC
        LIMIT ?, ?
        """;
    
    // 批量插入SQL
    private static final String BATCH_INSERT_USERS = """
        INSERT INTO sys_user (
            username, password, nickname, email, phone, 
            status, dept_id, create_by, create_time
        ) VALUES 
        %s
        """;
    
    // 统计查询SQL
    private static final String USER_STATISTICS = """
        SELECT 
            COUNT(*) as total_users,
            COUNT(CASE WHEN status = 1 THEN 1 END) as active_users,
            COUNT(CASE WHEN status = 0 THEN 1 END) as inactive_users,
            COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) as today_new_users,
            COUNT(CASE WHEN DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 END) as week_new_users
        FROM sys_user 
        WHERE deleted = 0
        """;
}
```

### 4.2 JSON模板定义

```java
@Service
public class NotificationService {
    
    // 邮件通知模板
    private static final String EMAIL_NOTIFICATION_TEMPLATE = """
        {
            "to": "%s",
            "subject": "%s",
            "template": "notification",
            "data": {
                "username": "%s",
                "title": "%s",
                "content": "%s",
                "timestamp": "%s",
                "action_url": "%s"
            }
        }
        """;
    
    // 微信消息模板
    private static final String WECHAT_MESSAGE_TEMPLATE = """
        {
            "touser": "%s",
            "template_id": "%s",
            "data": {
                "first": {
                    "value": "%s",
                    "color": "#173177"
                },
                "keyword1": {
                    "value": "%s",
                    "color": "#173177"
                },
                "keyword2": {
                    "value": "%s",
                    "color": "#173177"
                },
                "remark": {
                    "value": "%s",
                    "color": "#173177"
                }
            }
        }
        """;
    
    public void sendEmailNotification(String email, String subject, String username, 
                                    String title, String content, String actionUrl) {
        String json = String.format(EMAIL_NOTIFICATION_TEMPLATE,
            email, subject, username, title, content, 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionUrl
        );
        // 发送邮件逻辑
    }
}
```

### 4.3 HTML模板定义

```java
@Service
public class ReportService {
    
    // HTML报表模板
    private static final String REPORT_HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>%s</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                .header { background-color: #f5f5f5; padding: 10px; border-radius: 5px; }
                .content { margin: 20px 0; }
                .table { width: 100%%; border-collapse: collapse; }
                .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                .table th { background-color: #f2f2f2; }
                .footer { margin-top: 20px; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>%s</h1>
                <p>生成时间: %s</p>
            </div>
            <div class="content">
                %s
            </div>
            <div class="footer">
                <p>此报表由系统自动生成</p>
            </div>
        </body>
        </html>
        """;
    
    public String generateReport(String title, String content) {
        return String.format(REPORT_HTML_TEMPLATE,
            title, title, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            content
        );
    }
}
```

## 5. instanceof模式匹配的应用

### 5.1 类型判断和转换

```java
@Service
public class DataProcessor {
    
    // 传统写法（不推荐）
    public String processDataOld(Object data) {
        if (data instanceof String) {
            String str = (String) data;
            return "字符串: " + str.toUpperCase();
        } else if (data instanceof Integer) {
            Integer num = (Integer) data;
            return "整数: " + (num * 2);
        } else if (data instanceof List) {
            List<?> list = (List<?>) data;
            return "列表大小: " + list.size();
        }
        return "未知类型";
    }
    
    // JDK21模式匹配（推荐）
    public String processData(Object data) {
        return switch (data) {
            case String str -> "字符串: " + str.toUpperCase();
            case Integer num -> "整数: " + (num * 2);
            case List<?> list -> "列表大小: " + list.size();
            case null -> "空值";
            default -> "未知类型: " + data.getClass().getSimpleName();
        };
    }
    
    // 复杂对象处理
    public String handleRequest(Object request) {
        if (request instanceof UserCreateReqDTO userReq) {
            return "创建用户: " + userReq.username();
        } else if (request instanceof UserUpdateReqDTO userReq) {
            return "更新用户: " + userReq.id();
        } else if (request instanceof UserQueryReqDTO userReq) {
            return "查询用户: " + userReq.username();
        }
        return "未知请求类型";
    }
}
```

### 5.2 异常处理

```java
@Component
public class ExceptionHandler {
    
    public String handleException(Exception ex) {
        return switch (ex) {
            case ServiceException serviceEx -> 
                "业务异常: " + serviceEx.getMessage();
            case ValidationException validationEx -> 
                "参数校验异常: " + validationEx.getMessage();
            case DataAccessException dataEx -> 
                "数据访问异常: " + dataEx.getMessage();
            case SecurityException securityEx -> 
                "安全异常: " + securityEx.getMessage();
            default -> "系统异常: " + ex.getMessage();
        };
    }
    
    // 带条件的模式匹配
    public String categorizeException(Exception ex) {
        if (ex instanceof ServiceException serviceEx && serviceEx.getCode() == 400) {
            return "客户端错误: " + serviceEx.getMessage();
        } else if (ex instanceof ServiceException serviceEx && serviceEx.getCode() >= 500) {
            return "服务器错误: " + serviceEx.getMessage();
        } else if (ex instanceof RuntimeException runtimeEx) {
            return "运行时异常: " + runtimeEx.getMessage();
        }
        return "其他异常: " + ex.getMessage();
    }
}
```

## 6. 密封类(Sealed Classes)的应用

### 6.1 状态机建模

```java
// 订单状态密封类
public sealed interface OrderState 
    permits PendingState, PaidState, ShippedState, DeliveredState, CancelledState {
    
    String getDisplayName();
    boolean canTransitionTo(OrderState newState);
}

public record PendingState() implements OrderState {
    @Override
    public String getDisplayName() {
        return "待支付";
    }
    
    @Override
    public boolean canTransitionTo(OrderState newState) {
        return newState instanceof PaidState || newState instanceof CancelledState;
    }
}

public record PaidState() implements OrderState {
    @Override
    public String getDisplayName() {
        return "已支付";
    }
    
    @Override
    public boolean canTransitionTo(OrderState newState) {
        return newState instanceof ShippedState || newState instanceof CancelledState;
    }
}

public record ShippedState(String trackingNumber) implements OrderState {
    @Override
    public String getDisplayName() {
        return "已发货";
    }
    
    @Override
    public boolean canTransitionTo(OrderState newState) {
        return newState instanceof DeliveredState;
    }
}

public record DeliveredState(LocalDateTime deliveredAt) implements OrderState {
    @Override
    public String getDisplayName() {
        return "已送达";
    }
    
    @Override
    public boolean canTransitionTo(OrderState newState) {
        return false; // 已送达是终态
    }
}

public record CancelledState(String reason) implements OrderState {
    @Override
    public String getDisplayName() {
        return "已取消";
    }
    
    @Override
    public boolean canTransitionTo(OrderState newState) {
        return false; // 已取消是终态
    }
}
```

### 6.2 API结果建模

```java
// API调用结果密封类
public sealed interface ApiResult<T> 
    permits ApiResult.Success, ApiResult.Error, ApiResult.Loading {
    
    record Success<T>(T data) implements ApiResult<T> {}
    record Error<T>(String message, int code) implements ApiResult<T> {}
    record Loading<T>() implements ApiResult<T> {}
    
    // 工厂方法
    static <T> ApiResult<T> success(T data) {
        return new Success<>(data);
    }
    
    static <T> ApiResult<T> error(String message, int code) {
        return new Error<>(message, code);
    }
    
    static <T> ApiResult<T> loading() {
        return new Loading<>();
    }
    
    // 模式匹配处理
    default String getStatusMessage() {
        return switch (this) {
            case Success<T> success -> "请求成功";
            case Error<T> error -> "请求失败: " + error.message();
            case Loading<T> loading -> "请求中...";
        };
    }
}
```

## 7. 虚拟线程(Virtual Threads)的应用

### 7.1 异步任务处理

```java
@Service
public class AsyncTaskService {
    
    // 使用虚拟线程处理大量并发任务
    public void processBatchTasks(List<Task> tasks) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<TaskResult>> futures = tasks.stream()
                .map(task -> executor.submit(() -> processTask(task)))
                .toList();
            
            // 等待所有任务完成
            for (Future<TaskResult> future : futures) {
                try {
                    TaskResult result = future.get();
                    handleTaskResult(result);
                } catch (Exception e) {
                    log.error("任务执行失败", e);
                }
            }
        }
    }
    
    // 并发调用外部API
    public List<UserInfo> fetchUserInfosConcurrently(List<Long> userIds) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<UserInfo>> futures = userIds.stream()
                .map(userId -> executor.submit(() -> fetchUserInfo(userId)))
                .toList();
            
            return futures.stream()
                .map(future -> {
                    try {
                        return future.get(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.warn("获取用户信息失败: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        }
    }
    
    private TaskResult processTask(Task task) {
        // 模拟任务处理
        try {
            Thread.sleep(1000); // 虚拟线程不会阻塞OS线程
            return new TaskResult(task.getId(), "SUCCESS");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new TaskResult(task.getId(), "INTERRUPTED");
        }
    }
}
```

### 7.2 Web请求处理

```java
@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    // 使用虚拟线程处理耗时操作
    @PostMapping("/users")
    public CompletableFuture<CommonResult<UserRespDTO>> createUser(
            @RequestBody @Valid UserCreateReqDTO reqDTO) {
        
        return CompletableFuture.supplyAsync(() -> {
            // 创建用户
            UserRespDTO user = userService.createUser(reqDTO);
            
            // 异步发送通知（使用虚拟线程）
            Thread.startVirtualThread(() -> {
                notificationService.sendWelcomeEmail(user.getEmail());
                notificationService.sendSmsNotification(user.getPhone());
            });
            
            return CommonResult.success(user);
        }, Executors.newVirtualThreadPerTaskExecutor());
    }
}
```

## 8. 最佳实践总结

### 8.1 使用建议

1. **Record类适用场景**：
   - DTO对象定义
   - 配置类定义
   - 事件对象定义
   - 不可变数据载体

2. **Switch表达式适用场景**：
   - 状态处理逻辑
   - 类型转换
   - 枚举值处理
   - 简单的条件分支

3. **文本块适用场景**：
   - SQL语句定义
   - JSON/XML模板
   - HTML模板
   - 多行字符串常量

4. **模式匹配适用场景**：
   - 类型判断和转换
   - 异常处理
   - 复杂对象解构

5. **密封类适用场景**：
   - 状态机建模
   - API结果建模
   - 有限状态集合

6. **虚拟线程适用场景**：
   - 大量并发任务
   - I/O密集型操作
   - 异步任务处理

### 8.2 注意事项

1. **Record类限制**：
   - 不能继承其他类
   - 不能声明实例字段
   - 所有字段都是final的

2. **Switch表达式要求**：
   - 必须覆盖所有可能的情况
   - 每个分支必须返回相同类型

3. **虚拟线程注意**：
   - 不适用于CPU密集型任务
   - 避免使用synchronized
   - 优先使用ReentrantLock

### 8.3 迁移策略

1. **渐进式迁移**：
   - 新代码优先使用新特性
   - 重构时逐步替换旧代码
   - 保持向后兼容性

2. **团队培训**：
   - 组织JDK21新特性培训
   - 建立代码审查标准
   - 分享最佳实践

3. **工具支持**：
   - 配置IDE支持JDK21
   - 更新构建工具版本
   - 配置代码格式化规则

通过合理使用JDK21的新特性，可以显著提升代码的简洁性、可读性和维护性，同时提高开发效率和系统性能。