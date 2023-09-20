package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUpdatedDto {

    private Long id;

    @Length(min = 1, max = 128, message = "Title must be between 1 and 128 characters")
    @NotBlank(message = "Title cannot be null or blank")
    private String title;

    @Length(min = 1, max = 4096, message = "Description must be between 1 and 4096 characters")
    @NotBlank(message = "Description cannot be null or blank")
    private String description;
}
