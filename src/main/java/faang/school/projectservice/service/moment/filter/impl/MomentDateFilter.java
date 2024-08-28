package faang.school.projectservice.service.moment.filter.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getDate() != null;
    }

    @Override
    public Stream<Moment> apply(List<Moment> moments, MomentFilterDto filter) {
        return moments.stream().filter(moment -> moment.getDate().getMonth() == filter.getDate().getMonth());
    }
}
