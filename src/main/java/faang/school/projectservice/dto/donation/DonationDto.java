package faang.school.projectservice.dto.donation;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.Campaign;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DonationDto {
    @NotNull
    private Long id;
    @NotNull
    private Long paymentNumber;
    private BigDecimal amount;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime donationTime;
    @NotNull
    private CampaignDto campaignDto;
    private Currency currency;
    @NotNull
    private Long userId;
}
