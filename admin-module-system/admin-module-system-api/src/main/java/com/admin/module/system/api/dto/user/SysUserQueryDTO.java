package com.admin.module.system.api.dto.user;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统用户查询请求对象")
public class SysUserQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号", example = "admin")
    private String username;

    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    @Schema(description = "手机号码", example = "13888888888")
    private String phone;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "创建开始时间", example = "2024-01-01")
    private String beginTime;

    @Schema(description = "创建结束时间", example = "2024-12-31")
    private String endTime;
}