package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeetDto {
    @Size(max = 128)
    private String title;

    @Size(max = 512)
    private String description;
}
