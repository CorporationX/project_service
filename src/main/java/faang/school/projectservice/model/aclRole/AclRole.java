package faang.school.projectservice.model.aclRole;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum AclRole {
    NONE("none"),
    FREE_BUSY_READER("freeBusyReader"),
    READER("reader"),
    WRITER("writer"),
    OWNER("owner");

    private static final Map<String, AclRole> map;

    static {
        map = Arrays.stream(AclRole.values())
                .collect(Collectors.toMap(AclRole::getRole, Function.identity()));
    }

    @JsonValue
    private final String role;

    AclRole(String role) {
        this.role = role;
    }

    public static AclRole findByKey(String v) {
        return map.get(v);
    }
}