package com.admin.module.system.api.vo.role;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统角色展示对象")
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "角色编码", example = "SUPER_ADMIN")
    private String roleCode;

    @Schema(description = "角色描述", example = "拥有系统所有权限的超级管理员")
    private String roleDesc;

    @Schema(description = "显示顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "角色状态", example = "1", allowableValues = {"0", "1"})
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

    @Schema(description = "备注信息", example = "系统内置角色")
    private String remark;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "乐观锁版本号", example = "1")
    private Integer version;
}