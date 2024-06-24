package faang.school.projectservice.dto.project.calendar;

import faang.school.projectservice.model.aclRole.AclScopeType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScopeDto {
    @NotNull(message = "Scope type is required property of Acl.")
    private AclScopeType type;

    /**
     * The email address of a user or group, or the name of a domain, depending on the scope type.
     * Omitted for type "default".
     */
    private String value;
}
