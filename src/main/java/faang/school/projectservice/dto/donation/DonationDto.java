package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class DonationDto {

    @NotNull
    private Long paymentNumber;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Long campaign;
    @NotNull
    private Currency currency;
    @NotNull
    private Long userId;

}
