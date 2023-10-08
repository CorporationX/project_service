package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Builder
@Component
@RequiredArgsConstructor
public class DonationDateFilter implements DonationFilter {
    private final DonationRepository donationRepository;

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getDonationDate() != null;
    }

    @Override
    public List<Donation> apply(DonationFilterDto donationFilterDto) {
        LocalDate filterDate = donationFilterDto.getDonationDate();
        return donationRepository.findByDonationDate(filterDate);
    }
}
