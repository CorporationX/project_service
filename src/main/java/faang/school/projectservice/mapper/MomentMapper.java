package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MomentMapper {

    @Mapping(target = "createdBy", source = "creatorId")
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now())")
    Moment toEntity(CreateMomentRequest createMomentRequest, List<Project> projects, Long creatorId);

    CreateMomentResponse toCreateMomentResponse(Moment moment);

    GetMomentResponse toGetMomentResponse(Moment moment);

    List<GetMomentResponse> toGetMomentResponseList(List<Moment> moments);

    UpdateMomentResponse toUpdateMomentResponse(Moment moment);

    @Mapping(target = "userIds", ignore = true)
    @Mapping(target = "projects", ignore = true)
    void updateMoment(@MappingTarget Moment moment, UpdateMomentRequest updateMomentRequest, Long updatedBy);
}
