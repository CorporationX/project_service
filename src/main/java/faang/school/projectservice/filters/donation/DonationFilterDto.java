package faang.school.projectservice.filters.donation;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class DonationFilterDto {

    @NotNull
    private BigDecimal minAmount;

    @NotNull
    private BigDecimal maxAmount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime donationTime;

    @NotNull
    private Long campaignId;

    private Currency currency;

    private Long userId;
}
