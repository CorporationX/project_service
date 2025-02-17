package faang.school.projectservice.dto.donate;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DonationFilterDto(String currency,
                                Double minAmount,
                                Double maxAmount,
                                LocalDateTime createdDate) {
}
