package com.admin.module.system.api.vo.imports;

import com.admin.module.system.api.dto.imports.RoleImportDTO;
import com.admin.module.system.api.dto.imports.UserImportDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色导出校验结果类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleImportValidationResult {
    private boolean valid;
    private String message;
    private java.util.List<RoleImportDTO> previewData;
    private java.util.List<String> errors;
    private int totalRows;
    private int validRows;
    private int errorRows;

}
 