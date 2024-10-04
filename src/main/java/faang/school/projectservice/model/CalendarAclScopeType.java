package faang.school.projectservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarAclScopeType {
    //Доступ дается юзеру, указываемому по email
    USER("user"),

    //Доступ дается группе юзеров, указываемой по email группы
    GROUP("group"),

    //Доступ дается всем юзерам в определенном домене(например, домен.com или другойдомен.ru)
    DOMAIN("domain"),

    //Доступ дается всем юзерам
    DEFAULT("default");

    private final String scopeType;
}
