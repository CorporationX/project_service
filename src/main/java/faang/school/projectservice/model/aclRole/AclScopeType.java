package faang.school.projectservice.model.aclRole;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum AclScopeType {
    DEFAULT("default"),
    USER("user"),
    GROUP("group"),
    DOMAIN("domain");

    private static final Map<String, AclScopeType> map;

    static {
        map = new HashMap<>();
        for (AclScopeType v : AclScopeType.values()) {
            map.put(v.type, v);
        }
    }

    @JsonValue
    private final String type;

    AclScopeType(String type) {
        this.type = type;
    }

    public static AclScopeType findByKey(String v) {
        return map.get(v);
    }
}
