package faang.school.projectservice.dto.project;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    private Long parentId;
    private String name;
    private String description;
}