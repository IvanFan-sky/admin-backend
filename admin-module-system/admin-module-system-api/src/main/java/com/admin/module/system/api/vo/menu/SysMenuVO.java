package com.admin.module.system.api.vo.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单响应VO
 * 
 * 用于向前端返回菜单信息
 * 支持树形结构数据和格式化的时间字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统菜单展示对象")
public class SysMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    @Schema(description = "菜单类型", example = "2", allowableValues = {"1", "2", "3"})
    private Integer menuType;

    /**
     * 菜单类型显示文本
     * 根据menuType字段动态生成，用于前端显示
     */
    public String getMenuTypeText() {
        if (menuType == null) {
            return "未知";
        }
        return switch (menuType) {
            case 1 -> "目录";
            case 2 -> "菜单";
            case 3 -> "按钮";
            default -> "未知";
        };
    }

    @Schema(description = "路由地址", example = "/user")
    private String path;

    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    @Schema(description = "权限标识", example = "system:user:view")
    private String permission;

    @Schema(description = "菜单图标", example = "user")
    private String icon;

    @Schema(description = "显示顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "菜单状态", example = "1", allowableValues = {"0", "1"})
    private Integer visible;

    /**
     * 菜单状态显示文本
     * 根据visible字段动态生成，用于前端显示
     */
    public String getVisibleText() {
        if (visible == null) {
            return "未知";
        }
        return switch (visible) {
            case 0 -> "隐藏";
            case 1 -> "显示";
            default -> "未知";
        };
    }

    @Schema(description = "启用状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 状态显示文本
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

    @Schema(description = "是否为外链", example = "0", allowableValues = {"0", "1"})
    private Integer isFrame;

    /**
     * 外链状态显示文本
     */
    public String getIsFrameText() {
        if (isFrame == null) {
            return "否";
        }
        return switch (isFrame) {
            case 0 -> "否";
            case 1 -> "是";
            default -> "否";
        };
    }

    @Schema(description = "是否缓存", example = "0", allowableValues = {"0", "1"})
    private Integer isCache;

    /**
     * 缓存状态显示文本
     */
    public String getIsCacheText() {
        if (isCache == null) {
            return "不缓存";
        }
        return switch (isCache) {
            case 0 -> "不缓存";
            case 1 -> "缓存";
            default -> "不缓存";
        };
    }

    @Schema(description = "备注信息", example = "用户管理菜单")
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

    @Schema(description = "子菜单列表")
    private List<SysMenuVO> children = new ArrayList<>();

    public List<SysMenuVO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}