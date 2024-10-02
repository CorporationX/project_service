package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public class MomentYearFilter implements MomentFilter{
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.year() > 0;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> moment.getDate().getYear() == filters.year());
    }
}
