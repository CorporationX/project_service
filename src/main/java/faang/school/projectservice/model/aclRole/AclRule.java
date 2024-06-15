package faang.school.projectservice.model.aclRole;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum AclRule {
    NONE("none"),
    FREE_BUSY_READER("freeBusyReader"),
    READER("reader"),
    WRITER("writer"),
    OWNER("owner");

    private static final Map<String, AclRule> ACL_RULE_MAP = Arrays.stream(AclRule.values())
            .collect(Collectors.toMap(AclRule::getRole, Function.identity()));


    @JsonValue
    private final String role;

    AclRule(String role) {
        this.role = role;
    }

    public static AclRule findByKey(String v) {
        return ACL_RULE_MAP.get(v);
    }
}