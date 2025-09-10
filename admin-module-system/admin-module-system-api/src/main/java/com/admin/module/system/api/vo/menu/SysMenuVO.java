package com.admin.module.system.api.vo.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SysMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     * 0表示顶级菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     * 显示在界面上的菜单标题
     */
    private String menuName;

    /**
     * 菜单类型
     * 1-目录，2-菜单，3-按钮
     */
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

    /**
     * 路由地址
     * 访问的路由地址，如：`/user`
     */
    private String path;

    /**
     * 组件路径
     * 组件的具体路径，如：`system/user/index`
     */
    private String component;

    /**
     * 权限标识
     * 权限字符串，如：`system:user:view`
     */
    private String permission;

    /**
     * 菜单图标
     * 图标名称或图标类名
     */
    private String icon;

    /**
     * 显示顺序
     * 数值越小越靠前显示
     */
    private Integer sortOrder;

    /**
     * 菜单状态
     * 0-隐藏，1-显示
     */
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

    /**
     * 状态
     * 0-禁用，1-启用
     */
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

    /**
     * 是否为外链
     * 0-否，1-是
     */
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

    /**
     * 是否缓存
     * 0-不缓存，1-缓存
     */
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

    /**
     * 子菜单列表
     * 用于构建树形结构
     */
    private List<SysMenuVO> children = new ArrayList<>();

    public List<SysMenuVO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}