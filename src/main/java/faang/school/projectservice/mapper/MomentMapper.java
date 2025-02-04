package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment toMomentEntity(MomentCreateRequestDto momentCreateResponseDto);

    @Mapping(source = "userIds", target = "teamMembersIds")
    MomentResponseDto toMomentResponseDto (Moment moment);

    @Mapping(source = "userIds", target = "teamMembersIds")
    List<MomentResponseDto> toMomentResponseDtos(List<Moment> moments);

    List<ProjectResponseDto> toProjectDtos(List<Project> projects);


}
