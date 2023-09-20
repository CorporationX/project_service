package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto {

    private Long id;

    @Length(min = 1, max = 128, message = "Title must be between 1 and 128 characters")
    @NotBlank(message = "Title cannot be null or blank")
    private String title;

    @Length(min = 1, max = 4096, message = "Description must be between 1 and 4096 characters")
    @NotBlank(message = "Description cannot be null or blank")
    private String description;

    @NotNull(message = "Goal cannot be null")
    private BigDecimal goal;

    @NotNull(message = "Campaign status cannot be null")
    private CampaignStatus status;

    @NotNull(message = "Project id cannot be null")
    private Long projectId;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;

    private Long updatedBy;
}
