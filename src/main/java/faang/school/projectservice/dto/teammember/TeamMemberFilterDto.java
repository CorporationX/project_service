package faang.school.projectservice.dto.teammember;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberFilterDto extends FilterDto {
    private String role;
    private String projectName;
}
