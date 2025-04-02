package faang.school.projectservice.exception.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserExceptionMessage {

    private static final String NOT_FOUND = "User %d not found";

    public static String getNotFound(long id) {
        return String.format(NOT_FOUND, id);
    }
}
