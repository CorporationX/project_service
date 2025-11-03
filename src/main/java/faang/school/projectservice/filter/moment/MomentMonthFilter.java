package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentMonthFilter implements MomentFilter{
    @Override
    public boolean isAplicable(SearchMomentDto momentDto) {
        return momentDto.createdAt() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, SearchMomentDto momentDto) {
        return moments.filter(
                moment -> moment.getCreatedAt().getMonth().equals(momentDto.createdAt().getMonth()));
    }
}
