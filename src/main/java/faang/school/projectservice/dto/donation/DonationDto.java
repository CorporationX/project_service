package faang.school.projectservice.dto.donation;

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
public class DonationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private Long paymentNumber;

    @NotNull
    private BigDecimal amount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime donationTime;

    @NotNull
    private Long campaignId;

    private Currency currency;

    private Long userId;
}
