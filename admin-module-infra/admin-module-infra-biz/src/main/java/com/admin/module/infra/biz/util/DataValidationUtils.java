package com.admin.module.infra.biz.util;

import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.UserImportDTO;
import com.admin.module.infra.api.dto.RoleImportDTO;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class DataValidationUtils {

    private static final Validator validator;
    
    // 常用正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,30}$");
    private static final Pattern ROLE_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 验证用户导入数据
     *
     * @param userImportDTO 用户导入DTO
     * @param rowNumber 行号
     * @return 错误列表
     */
    public static List<ImportErrorDetailVO> validateUserImportData(UserImportDTO userImportDTO, int rowNumber) {
        List<ImportErrorDetailVO> errors = new ArrayList<>();

        // 基础验证注解验证
        Set<ConstraintViolation<UserImportDTO>> violations = validator.validate(userImportDTO);
        for (ConstraintViolation<UserImportDTO> violation : violations) {
            ImportErrorDetailVO error = new ImportErrorDetailVO();
            error.setRowNumber(rowNumber);
            error.setColumnName(violation.getPropertyPath().toString());
            error.setOriginalValue(getFieldValue(userImportDTO, violation.getPropertyPath().toString()));
            error.setErrorType(ImportExportConstants.ErrorType.VALIDATION_ERROR);
            error.setErrorMessage(violation.getMessage());
            errors.add(error);
        }

        // 自定义业务验证
        if (StringUtils.hasText(userImportDTO.getUsername())) {
            if (!USERNAME_PATTERN.matcher(userImportDTO.getUsername()).matches()) {
                errors.add(createError(rowNumber, "username", userImportDTO.getUsername(),
                    ImportExportConstants.ErrorType.FORMAT_ERROR, "用户名格式不正确，只能包含字母、数字和下划线，长度4-30位"));
            }
        }

        if (StringUtils.hasText(userImportDTO.getEmail())) {
            if (!EMAIL_PATTERN.matcher(userImportDTO.getEmail()).matches()) {
                errors.add(createError(rowNumber, "email", userImportDTO.getEmail(),
                    ImportExportConstants.ErrorType.FORMAT_ERROR, "邮箱格式不正确"));
            }
        }

        if (StringUtils.hasText(userImportDTO.getPhone())) {
            if (!PHONE_PATTERN.matcher(userImportDTO.getPhone()).matches()) {
                errors.add(createError(rowNumber, "phone", userImportDTO.getPhone(),
                    ImportExportConstants.ErrorType.FORMAT_ERROR, "手机号格式不正确"));
            }
        }

        if (StringUtils.hasText(userImportDTO.getGender())) {
            if (!isValidGender(userImportDTO.getGender())) {
                errors.add(createError(rowNumber, "gender", userImportDTO.getGender(),
                    ImportExportConstants.ErrorType.VALIDATION_ERROR, "性别只能是：未知、男、女"));
            }
        }

        if (StringUtils.hasText(userImportDTO.getBirthday())) {
            if (!DATE_PATTERN.matcher(userImportDTO.getBirthday()).matches()) {
                errors.add(createError(rowNumber, "birthday", userImportDTO.getBirthday(),
                    ImportExportConstants.ErrorType.FORMAT_ERROR, "生日格式不正确，应为：yyyy-MM-dd"));
            }
        }

        if (StringUtils.hasText(userImportDTO.getStatus())) {
            if (!isValidStatus(userImportDTO.getStatus())) {
                errors.add(createError(rowNumber, "status", userImportDTO.getStatus(),
                    ImportExportConstants.ErrorType.VALIDATION_ERROR, "状态只能是：启用、禁用"));
            }
        }

        return errors;
    }

    /**
     * 验证角色导入数据
     *
     * @param roleImportDTO 角色导入DTO
     * @param rowNumber 行号
     * @return 错误列表
     */
    public static List<ImportErrorDetailVO> validateRoleImportData(RoleImportDTO roleImportDTO, int rowNumber) {
        List<ImportErrorDetailVO> errors = new ArrayList<>();

        // 基础验证注解验证
        Set<ConstraintViolation<RoleImportDTO>> violations = validator.validate(roleImportDTO);
        for (ConstraintViolation<RoleImportDTO> violation : violations) {
            ImportErrorDetailVO error = new ImportErrorDetailVO();
            error.setRowNumber(rowNumber);
            error.setColumnName(violation.getPropertyPath().toString());
            error.setOriginalValue(getFieldValue(roleImportDTO, violation.getPropertyPath().toString()));
            error.setErrorType(ImportExportConstants.ErrorType.VALIDATION_ERROR);
            error.setErrorMessage(violation.getMessage());
            errors.add(error);
        }

        // 自定义业务验证
        if (StringUtils.hasText(roleImportDTO.getRoleCode())) {
            if (!ROLE_CODE_PATTERN.matcher(roleImportDTO.getRoleCode()).matches()) {
                errors.add(createError(rowNumber, "roleCode", roleImportDTO.getRoleCode(),
                    ImportExportConstants.ErrorType.FORMAT_ERROR, "角色编码格式不正确，只能包含字母、数字和下划线"));
            }
        }

        if (StringUtils.hasText(roleImportDTO.getStatus())) {
            if (!isValidStatus(roleImportDTO.getStatus())) {
                errors.add(createError(rowNumber, "status", roleImportDTO.getStatus(),
                    ImportExportConstants.ErrorType.VALIDATION_ERROR, "状态只能是：启用、禁用"));
            }
        }

        if (roleImportDTO.getSortOrder() != null) {
            if (roleImportDTO.getSortOrder() < 0 || roleImportDTO.getSortOrder() > 9999) {
                errors.add(createError(rowNumber, "sortOrder", roleImportDTO.getSortOrder().toString(),
                    ImportExportConstants.ErrorType.VALIDATION_ERROR, "显示顺序必须在0-9999之间"));
            }
        }

        return errors;
    }

    /**
     * 批量验证数据
     *
     * @param dataList 数据列表
     * @param dataType 数据类型
     * @param startRowNumber 起始行号
     * @return 错误列表
     */
    public static List<ImportErrorDetailVO> batchValidateData(List<?> dataList, String dataType, int startRowNumber) {
        List<ImportErrorDetailVO> allErrors = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            int currentRow = startRowNumber + i;
            Object data = dataList.get(i);

            List<ImportErrorDetailVO> rowErrors = switch (dataType) {
                case ImportExportConstants.DataType.USER -> 
                    validateUserImportData((UserImportDTO) data, currentRow);
                case ImportExportConstants.DataType.ROLE -> 
                    validateRoleImportData((RoleImportDTO) data, currentRow);
                default -> {
                    log.warn("未知的数据类型: {}", dataType);
                    yield new ArrayList<>();
                }
            };

            allErrors.addAll(rowErrors);
        }

        return allErrors;
    }

    /**
     * 检查重复数据
     *
     * @param dataList 数据列表
     * @param dataType 数据类型
     * @param startRowNumber 起始行号
     * @return 错误列表
     */
    public static List<ImportErrorDetailVO> checkDuplicateData(List<?> dataList, String dataType, int startRowNumber) {
        List<ImportErrorDetailVO> errors = new ArrayList<>();
        
        if (ImportExportConstants.DataType.USER.equals(dataType)) {
            errors.addAll(checkUserDuplicates((List<UserImportDTO>) dataList, startRowNumber));
        } else if (ImportExportConstants.DataType.ROLE.equals(dataType)) {
            errors.addAll(checkRoleDuplicates((List<RoleImportDTO>) dataList, startRowNumber));
        }

        return errors;
    }

    private static List<ImportErrorDetailVO> checkUserDuplicates(List<UserImportDTO> userList, int startRowNumber) {
        List<ImportErrorDetailVO> errors = new ArrayList<>();
        
        for (int i = 0; i < userList.size(); i++) {
            UserImportDTO user1 = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                UserImportDTO user2 = userList.get(j);
                
                if (StringUtils.hasText(user1.getUsername()) && user1.getUsername().equals(user2.getUsername())) {
                    errors.add(createError(startRowNumber + j, "username", user2.getUsername(),
                        ImportExportConstants.ErrorType.DUPLICATE_ERROR, "用户名重复"));
                }
                
                if (StringUtils.hasText(user1.getEmail()) && user1.getEmail().equals(user2.getEmail())) {
                    errors.add(createError(startRowNumber + j, "email", user2.getEmail(),
                        ImportExportConstants.ErrorType.DUPLICATE_ERROR, "邮箱重复"));
                }
                
                if (StringUtils.hasText(user1.getPhone()) && user1.getPhone().equals(user2.getPhone())) {
                    errors.add(createError(startRowNumber + j, "phone", user2.getPhone(),
                        ImportExportConstants.ErrorType.DUPLICATE_ERROR, "手机号重复"));
                }
            }
        }
        
        return errors;
    }

    private static List<ImportErrorDetailVO> checkRoleDuplicates(List<RoleImportDTO> roleList, int startRowNumber) {
        List<ImportErrorDetailVO> errors = new ArrayList<>();
        
        for (int i = 0; i < roleList.size(); i++) {
            RoleImportDTO role1 = roleList.get(i);
            for (int j = i + 1; j < roleList.size(); j++) {
                RoleImportDTO role2 = roleList.get(j);
                
                if (StringUtils.hasText(role1.getRoleCode()) && role1.getRoleCode().equals(role2.getRoleCode())) {
                    errors.add(createError(startRowNumber + j, "roleCode", role2.getRoleCode(),
                        ImportExportConstants.ErrorType.DUPLICATE_ERROR, "角色编码重复"));
                }
                
                if (StringUtils.hasText(role1.getRoleName()) && role1.getRoleName().equals(role2.getRoleName())) {
                    errors.add(createError(startRowNumber + j, "roleName", role2.getRoleName(),
                        ImportExportConstants.ErrorType.DUPLICATE_ERROR, "角色名称重复"));
                }
            }
        }
        
        return errors;
    }

    private static boolean isValidGender(String gender) {
        return "未知".equals(gender) || "男".equals(gender) || "女".equals(gender);
    }

    private static boolean isValidStatus(String status) {
        return "启用".equals(status) || "禁用".equals(status);
    }

    private static String getFieldValue(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static ImportErrorDetailVO createError(int rowNumber, String columnName, String originalValue,
                                                  String errorType, String errorMessage) {
        ImportErrorDetailVO error = new ImportErrorDetailVO();
        error.setRowNumber(rowNumber);
        error.setColumnName(columnName);
        error.setOriginalValue(originalValue);
        error.setErrorType(errorType);
        error.setErrorMessage(errorMessage);
        return error;
    }
}