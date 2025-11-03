package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isAplicable(SearchMomentDto momentDto);

    Stream<Moment> apply(Stream<Moment> moments, SearchMomentDto momentDto);
}
