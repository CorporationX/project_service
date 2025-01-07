package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    @NotNull
    private Long id;
    @NotNull
    private CampaignStatus status;
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private BigDecimal goal;
    @NotNull
    private Currency currency;
    @NotNull
    private BigDecimal amountRaised;
    @NotNull
    private Long projectId;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private Long createdBy;
    @NotNull
    private LocalDateTime updatedAt;
    @NotNull
    private Long updatedBy;
}
