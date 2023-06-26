package faang.school.projectservice.dto;

import faang.school.projectservice.model.Campaign;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

public class DonationDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime donationTime;
    private Campaign campaign;
    private BigDecimal sum;
    private Currency currency;
}
