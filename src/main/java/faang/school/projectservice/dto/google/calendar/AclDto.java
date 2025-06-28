package faang.school.projectservice.dto.google.calendar;

import faang.school.projectservice.model.google.calendar.Role;
import faang.school.projectservice.model.google.calendar.ScopeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AclDto {
    private String ruleId;
    @NotNull
    private Role role;
    @NotNull
    private ScopeType scopeType;
    @NotBlank
    private String scopeValue;
}
