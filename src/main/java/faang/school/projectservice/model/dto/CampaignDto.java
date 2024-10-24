package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.CampaignStatus;
import faang.school.projectservice.model.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class CampaignDto {
    private Long id;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
    @NotNull
    private Long projectId;
    @NotNull
    private Currency currency;
    @NotNull
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
}
