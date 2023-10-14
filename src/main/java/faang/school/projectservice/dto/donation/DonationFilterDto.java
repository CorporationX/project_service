package faang.school.projectservice.dto.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationFilterDto {
    private String currency;
    private BigDecimal amount;
    private LocalDate donationDate;
}
