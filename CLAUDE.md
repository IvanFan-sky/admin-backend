# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java Spring Boot 3.x admin management system backend using a multi-module single-instance architecture. The project is built with Java 21, Spring Boot 3.2.1, MyBatis-Plus 3.5.5, and follows modern development practices.

## Build Commands

```bash
# Build the project
mvn clean compile

# Package the application
mvn clean package

# Build and skip tests
mvn clean package -DskipTests

# Run the application (from admin-server directory)
cd admin-server
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Test Commands

```bash
# Run all tests
mvn test

# Run tests for specific module
cd admin-module-system/admin-module-system-biz
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest
```

## Development Commands

```bash
# Generate sources (including MapStruct mapping)
mvn generate-sources

# Clean and regenerate all
mvn clean generate-sources compile

# Check for dependency conflicts
mvn dependency:tree

# Update dependencies (if needed)
mvn versions:display-dependency-updates
```

## Architecture Overview

This is a **modular single-instance Spring Boot application** with the following layer structure:

### Multi-Module Architecture

```
admin-backend/
├── admin-dependencies/          # Maven BOM for dependency management  
├── admin-framework/             # Reusable Spring Boot starters
├── admin-module-system/         # Core system management module
│   ├── admin-module-system-api/ # API definitions (DTOs, interfaces)
│   └── admin-module-system-biz/ # Business logic implementation
├── admin-module-infra/          # Infrastructure module (similar structure)
├── admin-common/                # Shared utilities and base classes
└── admin-server/                # Main application starter
```

### Key Patterns

1. **API-BIZ Separation**: Each business module splits into API (interfaces/DTOs) and BIZ (implementation) sub-modules
2. **Framework Starters**: Custom Spring Boot starters in admin-framework/ for reusable components
3. **Dependency Management**: All versions managed centrally in admin-dependencies/pom.xml
4. **Layered Architecture**: Controller → Service → Mapper pattern within each module

## Technology Stack

- **Java**: 21 (required)
- **Spring Boot**: 3.2.1
- **Database**: MyBatis-Plus 3.5.5 with MySQL 8.0
- **Object Mapping**: MapStruct 1.5.5.Final
- **Cache**: Redis with Redisson 3.25.2
- **Documentation**: Knife4j 4.4.0 (OpenAPI 3.0)
- **Utilities**: Hutool 5.8.24
- **Validation**: Jakarta Validation 3.0.2

## Key Coding Patterns

### Entity and DTO Structure
- **DO**: Data Objects (database entities) in dal/dataobject/
- **DTO**: Request/Response DTOs in api/dto/
- **VO**: View Objects for API responses in api/vo/
- **Convert**: MapStruct converters in biz/convert/

### Service Layer Pattern
```java
// Interface in API module
public interface UserService {
    Long createUser(UserCreateDTO reqVO);
    PageResult<UserDO> getUserPage(UserPageDTO reqVO);
}

// Implementation in BIZ module
@Service
@Validated
public class UserServiceImpl implements UserService {
    // Business logic here
}
```

### Controller Pattern
```java
@RestController
@RequestMapping("/admin-api/system/users")
@Tag(name = "用户管理")
@Validated
public class UserController {
    
    @PostMapping("/create")
    @Operation(summary = "创建用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateDTO reqVO) {
        // Implementation
    }
}
```

### Mapper Pattern
```java
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    default PageResult<UserDO> selectPage(UserPageDTO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                .orderByDesc(UserDO::getId));
    }
}
```

## Configuration Structure

- **Main config**: admin-server/src/main/resources/application.yml
- **Environment configs**: application-{env}.yml (dev, prod)
- **Profile activation**: Uses @spring.profiles.active@ from Maven

## Database Conventions

### Table Naming
- Tables use `sys_` prefix for system tables
- All lowercase with underscore separation
- Example: `sys_user`, `sys_role`, `sys_menu`

### Common Fields
All business tables include:
- `id` (BIGINT, primary key, auto-increment)
- `create_time` (DATETIME)
- `update_time` (DATETIME) 
- `create_by` (VARCHAR)
- `update_by` (VARCHAR)
- `deleted` (TINYINT, logical delete flag)

## Important Development Notes

### Module Dependencies
- Server module depends on all BIZ modules
- BIZ modules depend on their own API module + other needed API modules
- API modules should have minimal dependencies
- Common module provides shared utilities

### Inter-Module Communication
- Use API interfaces for cross-module calls
- Avoid direct service dependencies across modules
- Event-driven communication for loose coupling

### Code Generation Integration
- Uses MyBatis-Plus code generator
- MapStruct for automated DTO/DO conversions
- Knife4j for automatic API documentation

### Security Implementation
- Spring Security 6.x with JWT tokens
- Method-level security with @PreAuthorize
- Permission-based access control (RBAC)

## Running the Application

1. Ensure Java 21 is installed
2. Set up MySQL 8.0+ database
3. Configure Redis server
4. Update database connection in application-dev.yml
5. Run from admin-server: `mvn spring-boot:run`
6. Access API docs at: http://localhost:8080/doc.html

The application uses Spring profiles for environment-specific configuration. Default profile is 'dev'.