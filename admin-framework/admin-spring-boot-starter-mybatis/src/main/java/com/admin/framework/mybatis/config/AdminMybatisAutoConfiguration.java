package com.admin.framework.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus自动配置类
 * 
 * 配置MyBatis-Plus的核心功能
 * 包括分页插件、乐观锁插件、字段自动填充等
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@AutoConfiguration
public class AdminMybatisAutoConfiguration {

    /**
     * 配置MyBatis-Plus拦截器
     * 
     * 集成分页插件和乐观锁插件
     * 提供统一的数据库操作增强功能
     *
     * @return MyBatis-Plus拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setDbType(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(1000L);
        paginationInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }

    /**
     * 配置字段自动填充处理器
     * 
     * 在数据插入和更新时自动填充审计字段
     * 包括创建时间、更新时间、创建人、更新人等
     *
     * @return 字段自动填充处理器实例
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入时的字段自动填充
             * 
             * @param metaObject 元数据对象
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                // 填充时间字段
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                // 填充标志字段
                this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
                this.strictInsertFill(metaObject, "version", Integer.class, 1);
                
                // 填充用户字段
                String username = getCurrentUsername();
                this.strictInsertFill(metaObject, "createBy", String.class, username);
                this.strictInsertFill(metaObject, "updateBy", String.class, username);
            }

            /**
             * 更新时的字段自动填充
             * 
             * @param metaObject 元数据对象
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                String username = getCurrentUsername();
                this.strictUpdateFill(metaObject, "updateBy", String.class, username);
            }
            
            /**
             * 获取当前用户名
             * TODO: 从安全上下文中获取当前登录用户
             * 
             * @return 当前用户名
             */
            private String getCurrentUsername() {
                return "system";
            }
        };
    }
}