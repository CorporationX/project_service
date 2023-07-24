package faang.school.projectservice.commonMessages.vacancy;

public final class ErrorMessagesForVacancy {
    public static final String INPUT_BODY_IS_NULL = "Input body from request is null!";
    public static final String PROJECT_NOT_EXIST_FORMAT = "Project id:{0} is not exist.";
    private ErrorMessagesForVacancy() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}