package faang.school.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {

    private Long id;

    @NotNull
    @Length(min = 1, max = 255)
    private String title;

    @Length(min = 1, max = 4096)
    private String description;

    @NotNull
    @Digits(integer = 17, fraction = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal goal;

    @NotNull
    @Digits(integer = 17, fraction = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal amountRaised;

    @NotNull
    @Positive
    private Long projectId;

    @Positive
    private Long creatorId;

    @NotNull
    private Currency currency;

    private Long updaterId;
    private CampaignStatus status;
}
