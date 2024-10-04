package faang.school.projectservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CalendarAclRole {
    //Удаляет все права доступа для юзера
    NONE("none"),

    //Дает юзеру видеть информацию о занятости в календаре, но не показывает полное содержание событий
    FREE_BUSY_READER("freeBusyReader"),

    //Дает юзеру читать все события календаря, но не вносить в них изменения
    READER("reader"),

    //Дает юзеру просматривать и редактировать события календаря, но не изменять параметры самого календаря
    WRITER("writer"),

    //Дает полный доступ к календарю(управление событиями, изменение настроек и прав доступа)
    OWNER("owner");

    private final String role;
}
