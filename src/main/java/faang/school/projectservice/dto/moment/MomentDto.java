package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private List<Long> projectIds;
    private List<Long> userIds;
    private String imageId;
}
