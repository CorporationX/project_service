package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationFilterServiceImpl implements DonationFilterService {

    private final List<DonationFilter> filters;

    @Override
    public Stream<Donation> applyFilters(Stream<Donation> donations, DonationFilterDto filterDto) {
        if (filterDto != null) {
            donations = filters.stream()
                    .filter(filter -> filter.isAcceptable(filterDto))
                    .reduce(donations, (acc, filter) -> filter.applyFilter(acc, filterDto), (a, b) -> b);
        }
        return donations;
    }
}