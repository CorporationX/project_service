package faang.school.projectservice.dto.donation;

public record DonationFilterDto(
        String currency,
        String minAmount,
        String maxAmount,
        String donationDate
) {
}
