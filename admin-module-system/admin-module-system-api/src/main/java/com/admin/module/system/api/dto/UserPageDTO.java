package com.admin.module.system.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户分页查询DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class UserPageDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 部门ID
     */
    private Long deptId;

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