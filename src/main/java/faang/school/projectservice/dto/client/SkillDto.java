package faang.school.projectservice.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Skill entity")
public class SkillDto {

    private long id;
    private String title;
}
