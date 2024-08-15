package faang.school.projectservice.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {
    }

    // project
    public static final String PROJECT_ALREADY_EXISTS_FOR_OWNER_ID = "You already created a project with this name. Please choose another name.";
    public static final String FAILED_PERSISTENCE = "Failed to save the project to database. There are some failed constraints. Please refer to support";
    public static final String FAILED_RETRIEVAL = "Failed to retrieve data from database. Please try again.";

    // event
    public static final String FAILED_EVENT = "Unable to send an event to message broker. Please trace it with id %s";

}
