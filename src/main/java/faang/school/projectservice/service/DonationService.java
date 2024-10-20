package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.dto.donation.DonationFilterDto;

public interface DonationService {
    DonationDto sendDonation(DonationDto donationDto);

    DonationDto getDonationById(long id);

    DonationDto getAllDonationsByUserId(long userId, DonationFilterDto filterDto);
}
