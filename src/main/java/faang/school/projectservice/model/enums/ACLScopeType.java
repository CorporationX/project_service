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
public enum ACLScopeType {
    DEFAULT("default"),
    USER("user"),
    GROUP("group"),
    DOMAIN("domain");

    private static final Map<String, ACLScopeType> ACL_SCOPE_TYPE_MAP = Arrays.stream(ACLScopeType.values())
            .collect(Collectors.toMap(ACLScopeType::getType, Function.identity()));

    @JsonValue
    private final String type;

    public static ACLScopeType findByKey(String v) {
        return ACL_SCOPE_TYPE_MAP.get(v);
    }
}