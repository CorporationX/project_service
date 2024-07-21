package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InternshipError
{
    CANNOT_UPDATING_EXCEPTION("Cannot be updating internship with status COMPLETED"),
    CANNOT_TO_ADD_INTERNS_AFTER_START_EXCEPTION("You cannot add a member/-s after the start of the internship"),
    CANNOT_TO_ADDED_INTERNS("you cannot add these members because they are already trainees"),
    INTERNSHIP_DURATION_EXCEPTION("The internship should last no more than 3 months"),
    ABSENT_INTERN_ROLE_EXCEPTION("Intern must have role INTERN."),
    NON_EXISTING_PROJECT_EXCEPTION("This project is not found"),
    NON_EXISTING_INTERNSHIP_EXCEPTION("Cannot find internship in database"),
    NOT_FOUND_USER("User not found"),;

    private final String message;
}
