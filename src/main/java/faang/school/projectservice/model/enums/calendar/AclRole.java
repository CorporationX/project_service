package faang.school.projectservice.model.enums.calendar;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum AclRole {
    NONE("none"),
    FREE_BUSY_READER("freeBusyReader"),
    READER("reader"),
    WRITER("writer"),
    OWNER("owner");

    private static final Map<String, AclRole> ACL_RULE_MAP = Arrays.stream(AclRole.values())
            .collect(Collectors.toMap(AclRole::getRole, Function.identity()));

    @JsonValue
    private final String role;

    public static AclRole findByKey(String v) {
        return ACL_RULE_MAP.get(v);
    }
}