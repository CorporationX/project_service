package faang.school.projectservice.validator.donation;

import faang.school.projectservice.dto.donation.DonationDto;

public class DonationDtoValidator {

    public boolean validate(DonationDto donationDto) {
        if (donationDto.userId == null) {
            throw new IllegalArgumentException("User who donated must have an Id.");
        }
        if (donationDto.campaignId == null) {
            throw new IllegalArgumentException("Campaign id must present in order to send a donation.");
        }
        if (donationDto.paymentNumber != null) {
            throw new IllegalArgumentException("Payment number must present in order to send a donation.");
        }
        if (donationDto.amount == null) {
            throw new IllegalArgumentException("Donation amount must present in order to send a donation.");
        }

        return true;
    }
}
