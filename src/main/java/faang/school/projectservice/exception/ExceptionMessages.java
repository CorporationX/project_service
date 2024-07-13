package faang.school.projectservice.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {
    }

    public static final String PROJECT_ALREADY_EXISTS_FOR_OWNER_ID = "You already created a project with this name. Please choose another name.";
    public static final String PROJECT_FAILED_PERSISTENCE = "Failed to save the project to database. There are some failed constraints. Please refer to support";

}
