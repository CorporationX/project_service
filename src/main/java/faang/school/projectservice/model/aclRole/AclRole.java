package faang.school.projectservice.model.aclRole;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum AclRole {
    NONE("none"),
    FREE_BUSY_READER("freeBusyReader"),
    READER("reader"),
    WRITER("writer"),
    OWNER("owner");

    private static final Map<String, AclRole> map;

    static {
        map = new HashMap<>();
        for (AclRole v : AclRole.values()) {
            map.put(v.role, v);
        }
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