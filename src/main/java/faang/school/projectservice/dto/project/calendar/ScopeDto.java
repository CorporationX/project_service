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
    private String value;
}
