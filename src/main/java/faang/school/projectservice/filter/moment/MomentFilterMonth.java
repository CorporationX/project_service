package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentFilterMonth implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.month() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto filterDto, Stream<Moment> moments) {
        return moments
                .filter(moment -> moment.getDate().getMonth() == filterDto.month());
    }

}
