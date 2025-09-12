package com.admin.module.log.biz.service;

import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.service.LogExportService;
import com.admin.module.log.biz.convert.LoginLogConvert;
import com.admin.module.log.biz.convert.OperationLogConvert;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 日志导出服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogExportServiceImpl implements LogExportService {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public byte[] exportOperationLogs(OperationLogQueryDTO queryDTO) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<OperationLogDO> wrapper = new LambdaQueryWrapper<OperationLogDO>()
                    .like(queryDTO.getTitle() != null, OperationLogDO::getTitle, queryDTO.getTitle())
                    .eq(queryDTO.getBusinessType() != null, OperationLogDO::getBusinessType, queryDTO.getBusinessType())
                    .like(queryDTO.getOperName() != null, OperationLogDO::getOperName, queryDTO.getOperName())
                    .eq(queryDTO.getStatus() != null, OperationLogDO::getStatus, queryDTO.getStatus())
                    .eq(queryDTO.getRequestMethod() != null, OperationLogDO::getRequestMethod, queryDTO.getRequestMethod())
                    .eq(queryDTO.getOperatorType() != null, OperationLogDO::getOperatorType, queryDTO.getOperatorType())
                    .like(queryDTO.getOperLocation() != null, OperationLogDO::getOperLocation, queryDTO.getOperLocation())
                    .ge(queryDTO.getMinCostTime() != null, OperationLogDO::getCostTime, queryDTO.getMinCostTime())
                    .le(queryDTO.getMaxCostTime() != null, OperationLogDO::getCostTime, queryDTO.getMaxCostTime())
                    .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null,
                            OperationLogDO::getOperTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                    .orderByDesc(OperationLogDO::getId);

            List<OperationLogDO> logs = operationLogMapper.selectList(wrapper);
            return generateOperationLogCsv(logs);
        } catch (Exception e) {
            log.error("导出操作日志失败", e);
            throw new RuntimeException("导出操作日志失败", e);
        }
    }

    @Override
    public byte[] exportLoginLogs(LoginLogQueryDTO queryDTO) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<LoginLogDO> wrapper = new LambdaQueryWrapper<LoginLogDO>()
                    .like(queryDTO.getUserName() != null, LoginLogDO::getUserName, queryDTO.getUserName())
                    .eq(queryDTO.getLoginType() != null, LoginLogDO::getLoginType, queryDTO.getLoginType())
                    .like(queryDTO.getIpaddr() != null, LoginLogDO::getIpaddr, queryDTO.getIpaddr())
                    .eq(queryDTO.getStatus() != null, LoginLogDO::getStatus, queryDTO.getStatus())
                    .like(queryDTO.getBrowser() != null, LoginLogDO::getBrowser, queryDTO.getBrowser())
                    .like(queryDTO.getOs() != null, LoginLogDO::getOs, queryDTO.getOs())
                    .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null,
                            LoginLogDO::getLoginTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                    .orderByDesc(LoginLogDO::getId);

            List<LoginLogDO> logs = loginLogMapper.selectList(wrapper);
            return generateLoginLogCsv(logs);
        } catch (Exception e) {
            log.error("导出登录日志失败", e);
            throw new RuntimeException("导出登录日志失败", e);
        }
    }

    /**
     * 生成操作日志CSV
     */
    private byte[] generateOperationLogCsv(List<OperationLogDO> logs) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // 写入BOM头，解决Excel中文乱码问题
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            
            // 写入CSV头
            writer.write("日志ID,模块标题,业务类型,方法名称,请求方式,操作类别,操作人员,请求URL,主机地址,操作地点,操作状态,错误消息,操作时间,消耗时间\n");
            
            // 写入数据行
            for (OperationLogDO log : logs) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        escapeComma(String.valueOf(log.getId())),
                        escapeComma(log.getTitle()),
                        escapeComma(getBusinessTypeName(log.getBusinessType())),
                        escapeComma(log.getMethod()),
                        escapeComma(log.getRequestMethod()),
                        escapeComma(getOperatorTypeName(log.getOperatorType())),
                        escapeComma(log.getOperName()),
                        escapeComma(log.getOperUrl()),
                        escapeComma(log.getOperIp()),
                        escapeComma(log.getOperLocation()),
                        escapeComma(getStatusName(log.getStatus())),
                        escapeComma(log.getErrorMsg()),
                        escapeComma(log.getOperTime() != null ? log.getOperTime().format(DATE_FORMATTER) : ""),
                        escapeComma(String.valueOf(log.getCostTime()))
                ));
            }
            writer.flush();
        }
        return baos.toByteArray();
    }

    /**
     * 生成登录日志CSV
     */
    private byte[] generateLoginLogCsv(List<LoginLogDO> logs) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // 写入BOM头，解决Excel中文乱码问题
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            
            // 写入CSV头
            writer.write("日志ID,用户账号,登录类型,IP地址,登录地点,浏览器,操作系统,登录状态,提示消息,登录时间\n");
            
            // 写入数据行
            for (LoginLogDO log : logs) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        escapeComma(String.valueOf(log.getId())),
                        escapeComma(log.getUserName()),
                        escapeComma(getLoginTypeName(log.getLoginType())),
                        escapeComma(log.getIpaddr()),
                        escapeComma(log.getLoginLocation()),
                        escapeComma(log.getBrowser()),
                        escapeComma(log.getOs()),
                        escapeComma(getLoginStatusName(log.getStatus())),
                        escapeComma(log.getMsg()),
                        escapeComma(log.getLoginTime() != null ? log.getLoginTime().format(DATE_FORMATTER) : "")
                ));
            }
            writer.flush();
        }
        return baos.toByteArray();
    }

    /**
     * 转义逗号和引号
     */
    private String escapeComma(String str) {
        if (str == null) {
            return "";
        }
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(Integer businessType) {
        if (businessType == null) return "未知";
        switch (businessType) {
            case 0: return "其它";
            case 1: return "新增";
            case 2: return "修改";
            case 3: return "删除";
            case 4: return "授权";
            case 5: return "导出";
            case 6: return "导入";
            case 7: return "强退";
            case 8: return "生成代码";
            case 9: return "清空数据";
            default: return "未知";
        }
    }

    /**
     * 获取操作类别名称
     */
    private String getOperatorTypeName(Integer operatorType) {
        if (operatorType == null) return "未知";
        switch (operatorType) {
            case 0: return "其它";
            case 1: return "后台用户";
            case 2: return "手机端用户";
            default: return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return status == 0 ? "正常" : "异常";
    }

    /**
     * 获取登录类型名称
     */
    private String getLoginTypeName(Integer loginType) {
        if (loginType == null) return "未知";
        switch (loginType) {
            case 1: return "用户名密码";
            case 2: return "邮箱密码";
            case 3: return "手机验证码";
            case 4: return "第三方登录";
            default: return "未知";
        }
    }

    /**
     * 获取登录状态名称
     */
    private String getLoginStatusName(Integer status) {
        if (status == null) return "未知";
        return status == 0 ? "成功" : "失败";
    }
}