package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageFilterDto {
    private String name;
    private List<Long> roleIds;
}
