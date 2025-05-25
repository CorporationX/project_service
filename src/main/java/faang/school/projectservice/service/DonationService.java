package faang.school.projectservice.service;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;

import java.util.List;

public interface DonationService {

    DonationDto sendDonation(DonationDto donationDto);

    DonationDto getDonationByIdAndUserId(long donationId, long userId);

    List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto donationFilterDto);
}
