package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCampaignDto {
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @Positive(message = "Goal must be greater than 0")
    private BigDecimal goal;
    @NotNull(message = "Project id must not be null")
    @PositiveOrZero(message = "Project id must not be negative")
    private Long projectId;
    @NotNull(message = "Currency must not be null")
    private Currency currency;
}
