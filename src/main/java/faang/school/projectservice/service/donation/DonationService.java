package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.filter.DonationFilterDto;

import java.util.List;

public interface DonationService {
    DonationDto sendDonation(long userId, DonationCreateDto donationDto);

    DonationDto getDonationById(long donationId);

    List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto filter);
}
