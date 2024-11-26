package faang.school.projectservice.exception;

public enum InternshipValidationExceptionMessage {
    NULL_DTO_EXCEPTION("Cannot process null-valued dto."),
    NULL_INTERNSHIP_ID_EXCEPTION("Created internship must have an id."),
    EMPTY_INTERNSHIP_NAME_EXCEPTION("The internship must have a name."),
    NON_EXISTING_INTERN_EXCEPTION("Some of interns don't exist for passed ids."),
    INTERNSHIP_DATES_WRONG_ORDER_EXCEPTION("Start date cannot be after end date."),
    INTERNSHIP_DURATION_EXCEPTION("The internship must last no more than {0} months"),
    INTERNSHIP_IN_THE_PAST_CREATION_EXCEPTION("The internship mus be created in the future (not the past)."),
    NON_EXISTING_INTERNSHIP_EXCEPTION("Unable to find passed internship in database."),
    NON_EXISTING_PROJECT_EXCEPTION("The internship must be referred to an existing project."),
    FOREIGN_MENTOR_EXCEPTION("The mentor must be from the same project in which the internship is being created."),
    NEW_INTERNS_EXCEPTION("Internship in progress cannot be filled with new interns.");

    private final String message;

    InternshipValidationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}