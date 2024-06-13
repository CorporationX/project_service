package faang.school.projectservice.model.aclRole;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum AclScopeType {
    DEFAULT("default"),
    USER("user"),
    GROUP("group"),
    DOMAIN("domain");

    private static final Map<String, AclScopeType> map;

    static {
        map = Arrays.stream(AclScopeType.values())
                .collect(Collectors.toMap(AclScopeType::getType, Function.identity()));
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
