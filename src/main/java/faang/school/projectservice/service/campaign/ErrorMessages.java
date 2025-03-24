package faang.school.projectservice.service.campaign;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages {

    PROJECT_NOT_FOUND("Project not found"),
    PROJECT_NOT_FOUND_ID("Project with id {} not found"),
    CAMPAIGN_NOT_FOUND("Campaign not found"),
    CAMPAIGN_NOT_FOUND_ID("Campaign with id {} not found"),
    USER_HAVE_DIFFERENT_ROLE("User have different role"),
    USER_HAVE_DIFFERENT_ROLE_ID("User with id {} have different role"),
    USER_NOT_FOUND("User not found"),
    USER_NOT_FOUND_ID("User with id {} not found"),
    CAMPAIGN_TITLE_ALREADY_EXISTS("Campaign title already exists in this project"),
    CAMPAIGN_TITLE_ALREADY_EXISTS_ID("Campaign title already exists in this project with {} id"),
    CAMPAIGN_ALREADY_DELETED("Campaign already deleted in this project"),
    CAMPAIGN_ALREADY_DELETED_ID("Campaign already deleted in this project with {} id");

    private final String message;
}
