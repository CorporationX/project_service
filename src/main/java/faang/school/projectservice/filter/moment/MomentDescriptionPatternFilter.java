package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentDescriptionPatternFilter implements MomentFilter {
    @Override
    public Boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getMomentDescriptionPattern() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto momentFilterDto, Stream<Moment> moments) {
        return moments
                .filter(moment -> moment.getDescription().matches(".*" + momentFilterDto.getMomentDescriptionPattern() + ".*"));
    }
}
