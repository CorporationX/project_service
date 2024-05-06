package faang.school.projectservice.exception;

public enum InternshipValidationExceptionMessage {
    NULL_DTO_EXCEPTION("Cannot process null-valued dto."),
    EMPTY_INTERNSHIP_NAME_EXCEPTION("The internship must have a name."),
    EMPTY_INTERNSHIP_PROJECT_ID_EXCEPTION("The internship must be referred to a specific project."),
    EMPTY_INTERNSHIP_MENTOR_ID_EXCEPTION("The internship must have a mentor."),
    EMPTY_INTERNS_LIST_EXCEPTION("The internship can only be created with interns."),
    EMPTY_INTERN_ID_EXCEPTION("Cannot process null-valued intern id."),
    INTERNSHIP_DURATION_EXCEPTION("The internship must last no more than 3 months."),
    EMPTY_INTERNSHIP_STATUS_EXCEPTION("The internship must have a status (IN_PROGRESS or COMPLETED)."),
    NON_EXISTING_PROJECT_EXCEPTION("The internship must be referred to an existing project."),
    FOREIGN_MENTOR_EXCEPTION("The mentor must be from the same project in which the internship is being created.");

    private final String message;

    InternshipValidationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
