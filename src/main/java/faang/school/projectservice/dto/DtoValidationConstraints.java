package faang.school.projectservice.dto;

public final class DtoValidationConstraints {

    private DtoValidationConstraints() {
    }

    // validation rules
    public static final String PROJECT_NAME_PATTERN = "^[a-zA-Z0-9_ ]{3,50}$";
    public static final String PROJECT_DESCRIPTION_PATTERN = "^[a-zA-Z0-9_ ]{10,4096}$";
    public static final String PROJECT_STORAGE_SIZE_LOWER_LIMIT = "Storage size must be greater than or equal to 0";
    public static final String PROJECT_STORAGE_SIZE_UPPER_LIMIT = "Maximum storage size is 2 GB";
    public static final String PROJECT_MAX_STORAGE_SIZE_LOWER_LIMIT = "Max storage size must be greater than or equal to 0";
    public static final String PROJECT_MAX_STORAGE_SIZE_UPPER_LIMIT = "Maximum storage size is 2 GB";

    // messages
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String PROJECT_NAME_CONSTRAINT = "Project name must be between 3 and 50 characters";
    public static final String PROJECT_DESCRIPTION_CONSTRAINT = "Project description must not exceed 500 characters";
    public static final String PROJECT_NAME_REQUIRED = "Project name is required";
    public static final String PROJECT_DESCRIPTION_REQUIRED = "Project description is required";
}
