package faang.school.projectservice.service;

import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.filter.DonationFilterDto;

import java.util.List;

public interface DonationService {
    DonationDto sendDonation(DonationDto donationDto);

    DonationDto getDonation(Long donationId, Long userId);

    List<DonationDto> getDonations(Long userId, DonationFilterDto donationFilterDto);
}
