package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.dto.donation.SearchDonationDto;

import java.util.List;

public interface DonationService {

    DonationResponse createDonation(long userId, DonationCreateRequest donationCreateRequest, long campaignId);

    DonationResponse getDonation(long donationId, long userId);

    List<DonationResponse> getDonations(long userId, SearchDonationDto searchDonationDto);
}
