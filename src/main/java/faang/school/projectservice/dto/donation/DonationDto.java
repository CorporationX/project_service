package faang.school.projectservice.dto.donation;

public record DonationDto(
        Long id,
        Long paymentNumber,
        String amount,
        String donationTime,
        Long campaignId,
        String currency,
        Long userId
) {
}
