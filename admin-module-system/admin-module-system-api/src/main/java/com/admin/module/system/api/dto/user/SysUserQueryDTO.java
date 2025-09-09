package com.admin.module.system.api.dto.user;

import com.admin.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户查询请求DTO
 * 
 * 用于接收前端用户查询的条件参数
 * 继承分页查询基类，支持分页和排序
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     * 支持模糊查询
     */
    private String username;

    /**
     * 用户昵称  
     * 支持模糊查询
     */
    private String nickname;

    /**
     * 手机号码
     * 支持模糊查询
     */
    private String phone;

    /**
     * 用户状态
     * 1-正常 0-禁用，支持精确查询
     */
    private Integer status;

    /**
     * 创建开始时间
     * 格式：yyyy-MM-dd，用于时间范围查询
     */
    private String beginTime;

    /**
     * 创建结束时间
     * 格式：yyyy-MM-dd，用于时间范围查询
     */
    private String endTime;
}