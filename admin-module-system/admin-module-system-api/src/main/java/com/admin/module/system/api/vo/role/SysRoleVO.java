package com.admin.module.system.api.vo.role;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色响应VO
 * 
 * 用于向前端返回角色信息
 * 包含角色的完整信息和格式化的时间字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     * 显示给用户的角色名称，如"管理员"、"普通用户"
     */
    private String roleName;

    /**
     * 角色编码
     * 系统内部使用的角色标识，如"ADMIN"、"USER"
     */
    private String roleCode;

    /**
     * 角色描述
     * 详细说明该角色的职责和权限范围
     */
    private String roleDesc;

    /**
     * 显示顺序
     * 用于角色列表的显示排序，数值越小越靠前
     */
    private Integer sortOrder;

    /**
     * 角色状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 角色状态显示文本
     * 根据status字段动态生成，用于前端显示
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "禁用";
            case 1 -> "启用";
            default -> "未知";
        };
    }

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     * 格式化为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     * 格式化为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 乐观锁版本号
     * 用于并发控制
     */
    private Integer version;
}