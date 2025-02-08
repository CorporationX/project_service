package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetCreateRequest;
import faang.school.projectservice.dto.meet.MeetResponse;
import faang.school.projectservice.dto.meet.MeetUpdateRequest;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MeetMapper {
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "status", defaultValue = "PENDING")
    Meet toEntity(MeetCreateRequest meetCreateRequest);

    @Mapping(source = "project.id", target = "projectId")
    MeetResponse toMeetResponse(Meet meet);

    @Mapping(source = "projectId", target = "project.id")
    void updateMeet(MeetUpdateRequest meetUpdateRequest, @MappingTarget Meet meet);
}