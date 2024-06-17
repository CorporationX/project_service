package faang.school.projectservice.dto.project.calendar;

import faang.school.projectservice.model.aclRole.AclRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AclDto {
    private String id;
    @NotNull(message = "Role is required property of Acl.")
    private AclRole role;
    @Valid
    @NotNull(message = "Scope is required property of Acl.")
    private ScopeDto scope;
}
