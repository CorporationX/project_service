package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto) ;
    Stream<Moment> apply (List<Moment> momentList, MomentFilterDto filter) ;
}
