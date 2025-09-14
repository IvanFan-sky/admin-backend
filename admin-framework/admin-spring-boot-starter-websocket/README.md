# Admin WebSocket Starter

基于Spring Boot的WebSocket技术组件，提供实时推送功能。

## 功能特性

- 🚀 开箱即用的WebSocket支持
- 💬 多种消息类型（系统通知、公告、站内信等）
- 👥 用户会话管理
- 💓 心跳检测和自动重连
- 🔒 连接认证和权限控制
- 📊 在线用户统计
- 🎯 支持单用户、多用户、广播消息
- 🔧 灵活的配置选项
- 📱 SockJS支持（兼容不支持WebSocket的浏览器）

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.admin</groupId>
    <artifactId>admin-spring-boot-starter-websocket</artifactId>
    <version>${admin.version}</version>
</dependency>
```

### 2. 配置参数

```yaml
admin:
  websocket:
    enabled: true                    # 是否启用WebSocket
    endpoint: /websocket            # WebSocket端点路径
    allowed-origins: ["*"]          # 允许的跨域来源
    sockjs-enabled: true            # 是否启用SockJS支持
    heartbeat-interval: 25000       # 心跳间隔（毫秒）
    message-buffer-size: 8192       # 消息缓冲区大小
    max-session-idle-timeout: 60000 # 最大会话空闲时间（毫秒）
    auth-enabled: true              # 是否启用用户认证
```

### 3. 使用WebSocket API

```java
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketApi webSocketApi;

    /**
     * 发送系统通知给指定用户
     */
    @PostMapping("/notify/user/{userId}")
    public void notifyUser(@PathVariable Long userId, @RequestBody String message) {
        webSocketApi.sendSystemNotification(userId, "系统通知", message);
    }

    /**
     * 广播系统公告
     */
    @PostMapping("/notify/broadcast")
    public void broadcast(@RequestBody String message) {
        webSocketApi.sendSystemAnnouncement("系统公告", message);
    }

    /**
     * 发送站内信
     */
    @PostMapping("/message/send/{userId}")
    public void sendMessage(@PathVariable Long userId, @RequestBody String message) {
        webSocketApi.sendInternalMessage(userId, "新消息", message);
    }

    /**
     * 获取在线用户数
     */
    @GetMapping("/online/count")
    public int getOnlineCount() {
        return webSocketApi.getOnlineUserCount();
    }

    /**
     * 检查用户是否在线
     */
    @GetMapping("/online/check/{userId}")
    public boolean isUserOnline(@PathVariable Long userId) {
        return webSocketApi.isUserOnline(userId);
    }
}
```

## 前端连接示例

### 原生WebSocket

```javascript
// 连接WebSocket（需要传递用户ID）
const ws = new WebSocket('ws://localhost:8080/websocket?userId=123');

// 连接成功
ws.onopen = function(event) {
    console.log('WebSocket连接成功');
};

// 接收消息
ws.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('收到消息:', message);
    
    // 根据消息类型处理
    switch(message.type) {
        case 'system_notification':
            showNotification(message.title, message.content);
            break;
        case 'system_announcement':
            showAnnouncement(message.title, message.content);
            break;
        case 'internal_message':
            showInternalMessage(message.title, message.content);
            break;
        case 'online_user_count':
            updateOnlineCount(message.content);
            break;
    }
};

// 连接关闭
ws.onclose = function(event) {
    console.log('WebSocket连接关闭');
};

// 连接错误
ws.onerror = function(error) {
    console.error('WebSocket错误:', error);
};

// 发送心跳
setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
            type: 'heartbeat',
            content: 'ping'
        }));
    }
}, 30000);
```

### SockJS（兼容性更好）

```javascript
// 引入SockJS
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>

// 连接SockJS
const sock = new SockJS('http://localhost:8080/websocket/sockjs?userId=123');

// 其他处理逻辑与WebSocket相同
sock.onopen = function() {
    console.log('SockJS连接成功');
};

sock.onmessage = function(e) {
    const message = JSON.parse(e.data);
    console.log('收到消息:', message);
};
```

## 消息格式

### 消息结构

```json
{
    "messageId": "uuid",
    "type": "system_notification",
    "title": "消息标题",
    "content": "消息内容",
    "senderId": 1,
    "senderName": "发送者",
    "receiverId": 123,
    "receiverType": "user",
    "priority": 2,
    "needAck": false,
    "extra": {},
    "createTime": "2024-01-01 12:00:00"
}
```

### 消息类型

- `system_notification`: 系统通知
- `system_announcement`: 系统公告
- `internal_message`: 站内信
- `online_user_count`: 在线用户数更新
- `user_online`: 用户上线
- `user_offline`: 用户下线
- `heartbeat`: 心跳
- `ack`: 确认收到

### 优先级

- `1`: 低优先级
- `2`: 中优先级
- `3`: 高优先级
- `4`: 紧急

## 事件监听

组件支持Spring事件机制，可以监听系统事件自动发送WebSocket消息：

```java
@Component
public class BusinessEventListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishAnnouncement(String title, String content) {
        // 发布系统公告事件
        eventPublisher.publishEvent(
            new WebSocketEventListener.SystemAnnouncementEvent(title, content)
        );
    }

    public void sendMessage(Long userId, String title, String content) {
        // 发布站内信事件
        eventPublisher.publishEvent(
            new WebSocketEventListener.InternalMessageEvent(userId, title, content)
        );
    }
}
```

## 高级配置

### 自定义消息处理

```java
@Component
public class CustomWebSocketHandler extends WebSocketHandler {
    
    @Override
    protected void handleWebSocketMessage(WebSocketSession session, WebSocketMessage message) {
        // 自定义消息处理逻辑
        super.handleWebSocketMessage(session, message);
    }
}
```

### 自定义认证

```java
@Component
public class CustomWebSocketInterceptor extends WebSocketInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 自定义认证逻辑
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
```

## 监控和统计

```java
@RestController
public class WebSocketMonitorController {

    @Autowired
    private WebSocketApi webSocketApi;

    /**
     * 获取WebSocket统计信息
     */
    @GetMapping("/websocket/stats")
    public Map<String, Object> getStats() {
        return webSocketApi.getSessionStats();
    }

    /**
     * 获取在线用户列表
     */
    @GetMapping("/websocket/online-users")
    public Set<Long> getOnlineUsers() {
        return webSocketApi.getOnlineUserIds();
    }
}
```

## 注意事项

1. **用户认证**: 连接时必须提供有效的用户ID
2. **跨域配置**: 生产环境建议配置具体的允许域名
3. **心跳机制**: 客户端应定期发送心跳保持连接
4. **错误处理**: 客户端应处理连接断开和重连逻辑
5. **消息确认**: 重要消息建议启用确认机制

## 故障排查

### 连接失败

1. 检查用户ID是否正确传递
2. 检查跨域配置
3. 检查防火墙和代理设置

### 消息丢失

1. 检查网络连接稳定性
2. 启用消息确认机制
3. 检查心跳配置

### 性能问题

1. 调整心跳间隔
2. 优化消息内容大小
3. 使用消息队列缓冲

## 版本历史

- v1.0.0: 初始版本，基础WebSocket功能
- 后续版本将支持Redis集群、消息持久化等高级功能