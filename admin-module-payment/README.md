# 支付管理模块 (admin-module-payment)

## 📋 模块简介

支付管理模块是基于Spring Boot 3.x + JDK 21构建的企业级支付系统，提供完整的支付订单管理、多渠道支付接入、异步回调处理等功能。

## 🏗️ 模块架构

```
admin-module-payment/
├── admin-module-payment-api/          # API接口层
│   ├── dto/                          # 数据传输对象
│   │   └── order/                    # 支付订单相关DTO
│   ├── vo/                           # 值对象
│   │   └── order/                    # 支付订单相关VO
│   ├── enums/                        # 枚举类
│   └── service/                      # 服务接口
└── admin-module-payment-biz/          # 业务实现层
    ├── controller/                   # 控制器
    │   └── order/                    # 支付订单控制器
    ├── service/                      # 业务服务实现
    │   ├── order/                    # 支付订单服务
    │   └── channel/                  # 支付渠道配置服务
    ├── dal/                         # 数据访问层
    │   ├── dataobject/              # 数据对象
    │   └── mapper/                  # MyBatis映射器
    ├── convert/                     # 对象转换器
    ├── channel/                     # 支付渠道实现
    │   ├── core/                    # 核心抽象
    │   ├── mock/                    # 模拟支付实现
    │   ├── wechat/                  # 微信支付实现（预留）
    │   └── alipay/                  # 支付宝支付实现（预留）
    └── resources/
        └── mapper/                  # MyBatis XML映射文件
```

## ✨ 核心功能

### 🎯 支付订单管理
- ✅ 支付订单创建
- ✅ 订单状态查询
- ✅ 订单分页查询
- ✅ 订单状态同步
- ✅ 订单关闭处理

### 🔌 支付渠道支持
- ✅ **模拟支付** - 开发阶段使用，可配置成功率和延迟
- 🚧 **微信支付** - 预留接口，生产环境需接入官方SDK
- 🚧 **支付宝支付** - 预留接口，生产环境需接入官方SDK

### 📊 核心特性
- ✅ **统一抽象接口** - 基于策略模式的支付渠道抽象
- ✅ **配置化管理** - 支持多环境、多渠道配置
- ✅ **完整状态管理** - 订单状态机管理
- ✅ **异常处理** - 统一错误码和异常处理
- ✅ **对象转换** - MapStruct自动转换
- ✅ **API文档** - Swagger/OpenAPI自动生成

## 🗄️ 数据库设计

### 核心表结构

#### payment_order - 支付订单表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单ID |
| order_no | VARCHAR(64) | 订单号 |
| merchant_order_no | VARCHAR(64) | 商户订单号 |
| user_id | BIGINT | 用户ID |
| channel_code | VARCHAR(32) | 支付渠道编码 |
| payment_method | VARCHAR(32) | 支付方式 |
| amount | DECIMAL(10,2) | 支付金额 |
| status | TINYINT | 订单状态 |

#### payment_channel_config - 支付渠道配置表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 配置ID |
| channel_code | VARCHAR(32) | 渠道编码 |
| channel_name | VARCHAR(64) | 渠道名称 |
| status | TINYINT | 启用状态 |
| config | JSON | 配置信息 |

## 🚀 快速开始

### 1. 数据库初始化
```sql
-- 执行支付模块数据库脚本
source sql/payment_schema.sql;
```

### 2. 配置支付渠道
```sql
-- 模拟支付配置（默认已初始化）
INSERT INTO payment_channel_config (channel_code, channel_name, status, config) 
VALUES ('mock', '模拟支付', 1, '{"success_rate": 100, "delay_seconds": 2}');
```

### 3. API调用示例

#### 创建支付订单
```bash
POST /payment/order/create
Content-Type: application/json

{
    "merchantOrderNo": "ORDER_20250914_001",
    "userId": 1,
    "paymentMethod": "mock",
    "amount": 100.00,
    "subject": "测试商品",
    "body": "这是一个测试商品"
}
```

#### 查询支付订单
```bash
GET /payment/order/get-by-order-no?orderNo=PAY1726320000001234
```

## 🛠️ 开发指南

### 支付渠道扩展

1. **实现支付渠道接口**
```java
@Service
public class CustomPaymentChannelService implements PaymentChannelService {
    @Override
    public String getChannelCode() {
        return "custom";
    }
    
    @Override
    public PaymentResult pay(PaymentOrderDO order, PaymentChannelConfig config) {
        // 实现具体支付逻辑
    }
}
```

2. **添加渠道配置**
```sql
INSERT INTO payment_channel_config (channel_code, channel_name, status, config) 
VALUES ('custom', '自定义支付', 1, '{"key": "value"}');
```

### 支付方式扩展

1. **添加枚举定义**
```java
public enum PaymentMethodEnum {
    CUSTOM("custom", "自定义支付");
}
```

2. **更新渠道支持检查**
```java
@Override
public boolean isSupport(String paymentMethod) {
    return "custom".equals(paymentMethod);
}
```

## 🔧 配置说明

### 模拟支付配置
```json
{
    "success_rate": 100,     // 成功率 (0-100)
    "delay_seconds": 2       // 模拟延迟秒数
}
```

### 微信支付配置（预留）
```json
{
    "app_id": "",           // 应用ID
    "mch_id": "",           // 商户号
    "api_key": "",          // API密钥
    "cert_path": ""         // 证书路径
}
```

### 支付宝配置（预留）
```json
{
    "app_id": "",           // 应用ID
    "private_key": "",      // 私钥
    "public_key": "",       // 公钥
    "sign_type": "RSA2"     // 签名类型
}
```

## 🎨 设计思想

### 1. 渠道抽象设计
借鉴JeePay等成熟支付系统，采用统一的支付渠道接口：
- `PaymentChannelService` - 支付服务接口
- `PaymentResult` - 统一支付结果
- `PaymentChannelConfig` - 渠道配置封装

### 2. 状态管理
完整的订单状态流转：
```
待支付 -> 支付中 -> 支付成功/失败
       -> 已关闭 -> 已退款
```

### 3. 错误处理
统一的错误码管理：
- `1200001` - 支付订单不存在
- `1200002` - 支付订单已存在
- `1200005` - 不支持的支付方式

## 📝 待办事项

- [ ] 接入微信支付官方SDK
- [ ] 接入支付宝官方SDK  
- [ ] 实现支付回调处理
- [ ] 实现退款功能
- [ ] 添加支付统计报表
- [ ] 实现定时任务（订单超时处理）
- [ ] 添加支付安全校验

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](../LICENSE) 文件了解详情。

## 📞 技术支持

如有问题，请提交 [Issue](https://github.com/your-repo/issues) 或联系开发团队。
