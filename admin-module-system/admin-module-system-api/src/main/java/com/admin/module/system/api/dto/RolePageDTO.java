package com.admin.module.system.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色分页查询DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class RolePageDTO {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 10;
}