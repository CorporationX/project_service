package faang.school.projectservice.dto.calendar;

import faang.school.projectservice.model.enums.ACLScopeType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScopeDto {
    @NotNull(message = "Scope type is required property of ACL.")
    private ACLScopeType type;

    private String value;
}
