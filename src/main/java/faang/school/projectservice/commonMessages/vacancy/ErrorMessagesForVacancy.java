package faang.school.projectservice.commonMessages.vacancy;

public final class ErrorMessagesForVacancy {
    public static final String INPUT_BODY_IS_NULL = "Input body from request is null!";
    public static final String PROJECT_NOT_EXIST_FORMAT = "Project not found by id: {0}";
    public static final String ERROR_OWNER_ROLE_FORMAT = "The user id{0} cannot create a vacancy," +
            " because his role does not allow creating vacancies";
    public static final String VACANCY_ID_IS_NULL = "VacancyId is null!";
    public static final String NEGATIVE_VACANCY_ID = "VacancyId cannot be negative!";
    public static final String NAME_IS_NULL = "Name is null.";
    public static final String NAME_IS_BLANK = "The name field cannot be empty";
    public static final String DESCRIPTION_IS_NULL = "Description is null.";
    public static final String DESCRIPTION_IS_BLANK = "The description field cannot be empty";
    public static final String PROJECT_ID_IS_NULL = "ProjectID is null.";
    public static final String NEGATIVE_PROJECT_ID_FORMAT = "ProjectID cannot be negative. Provided id:{0}";
    public static final String CREATED_BY_ID_IS_NULL = "CreatedBy is null.";
    public static final String NEGATIVE_CREATED_BY_ID_FORMAT = "CreatedBy cannot be negative. Provided id:{0}";
    public static final String STATUS_IS_NULL = "Status is null.";
    public static final String VACANCY_NOT_EXIST_FORMAT = "Vacancy with id:{0} is not exist.";
    public static final String VACANCY_CANT_BE_CLOSED_FORMAT = "Vacancy id: {0} cannot be closed, because the number of participants is less {1}.";
    public static final String VACANCY_CANT_BE_CHANGED_FORMAT = "Vacancy cannot be changed. " +
            "The user role does not allow you to make changes. User roles {0}. Required roles one of MANAGER or OWNER";

    private ErrorMessagesForVacancy() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}