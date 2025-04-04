package faang.school.projectservice.validator.campaign;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages {

    FORBIDDEN_VALUE("You can't change forbidden value"),
    FORBIDDEN_VALUE_ID("You can't change amount raised, updaterId with {} id"),
    TITLE_EMPTY("Title can't be empty"),
    TITLE_EMPTY_ID("Title can't be empty, updaterId with {} id"),
    DESCRIPTION_EMPTY("Description can't be empty"),
    DESCRIPTION_EMPTY_ID("Description can't be empty, updaterId with {} id");

    private final String message;
}
