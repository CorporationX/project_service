package faang.school.projectservice.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ACLRole {
    NONE("none"),
    FREE_BUSY_READER("freeBusyReader"),
    READER("reader"),
    WRITER("writer"),
    OWNER("owner");

    private static final Map<String, ACLRole> ACL_RULE_MAP = Arrays.stream(ACLRole.values())
            .collect(Collectors.toMap(ACLRole::getRole, Function.identity()));

    @JsonValue
    private final String role;

    public static ACLRole findByKey(String v) {
        return ACL_RULE_MAP.get(v);
    }
}
