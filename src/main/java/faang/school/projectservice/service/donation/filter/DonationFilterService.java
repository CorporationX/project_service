package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilterService {
    Stream<Donation> applyFilters(Stream<Donation> donations, DonationFilterDto filterDto);
}