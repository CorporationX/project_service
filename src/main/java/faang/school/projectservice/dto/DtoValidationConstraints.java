package faang.school.projectservice.dto;

public final class DtoValidationConstraints {

    private DtoValidationConstraints() {
    }

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String PROJECT_NAME_CONSTRAINT = "Project name must be between 3 and 50 characters";
    public static final String PROJECT_DESCRIPTION_CONSTRAINT = "Project description must not exceed 500 characters";
    public static final String PROJECT_NAME_REQUIRED = "Project name is required";
    public static final String PROJECT_DESCRIPTION_REQUIRED = "Project description is required";
}
