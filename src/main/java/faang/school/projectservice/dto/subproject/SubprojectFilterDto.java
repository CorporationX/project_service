package faang.school.projectservice.dto.subproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubprojectFilterDto {
    private long id;
    private long requesterId;
    private String nameFilter;
    private String statusFilter;
}
