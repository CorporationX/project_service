package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;

import java.util.List;

public interface DonationService {

    DonationDto sendDonation(DonationDto donationDto);

    DonationDto getDonationById(long donationId);

    List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto donationFilterDto);
}
