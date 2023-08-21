package faang.school.projectservice.commonMessages.vacancy;

public final class ErrorMessagesForVacancy {
    public static final String INPUT_BODY_IS_NULL = "Input body from request is null!";
    public static final String PROJECT_NOT_EXIST_FORMAT = "Project not found by id: {0}";
    public static final String ERROR_OWNER_ROLE_FORMAT = "The user id{0} cannot create a vacancy," +
            " because his role doesn't allow creating vacancies";
    public static final String NAME_IS_NULL = "Name is null.";
    public static final String NAME_IS_BLANK = "The name field cannot be empty";
    public static final String DESCRIPTION_IS_NULL = "Description is null.";
    public static final String DESCRIPTION_IS_BLANK = "The description field cannot be empty";
    public static final String PROJECT_ID_IS_NULL = "ProjectID is null.";
    public static final String NEGATIVE_PROJECT_ID_FORMAT = "ProjectID cannot be negative. Provided id:{0}";
    public static final String CREATED_BY_ID_IS_NULL = "CreatedBy is null.";
    public static final String NEGATIVE_CREATED_BY_ID_FORMAT = "CreatedBy cannot be negative. Provided id:{0}";
    private ErrorMessagesForVacancy() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}
