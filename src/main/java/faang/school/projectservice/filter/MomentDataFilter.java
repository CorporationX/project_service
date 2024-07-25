package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentDataFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getDate() != null;
    }

    @Override
    public Stream<Moment> apply(List<Moment> momentList, MomentFilterDto momentFilterDto) {
        return momentList.stream().filter(moment->moment.getDate().getMonth() == momentFilterDto.getDate().getMonth());
    }

}
