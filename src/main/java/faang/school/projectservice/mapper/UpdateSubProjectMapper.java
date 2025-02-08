package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

@Mapper
public interface UpdateSubProjectMapper {
    UpdateSubProjectDto toDto(Project subProject);

    default MomentDto toMomentDto(Moment moment) {
        return MomentDto.builder()
                .id(moment.getId())
                .name(moment.getName())
                // и другие поля
                .build();
    }

    default Moment toMoment(MomentDto momentDto) {
        return Moment.builder()
                .id(momentDto.id())
                .name(momentDto.name())
                // и другие поля
                .build();
    }
}