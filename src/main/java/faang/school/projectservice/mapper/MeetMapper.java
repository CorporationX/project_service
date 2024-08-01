package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.meet.Meet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "status", target = "status", defaultValue = "SCHEDULED")
    @Mapping(source = "teamId", target = "team.id")
    Meet toEntity(MeetDto meetDto);


    @Mapping(source = "team.id", target = "teamId")
    MeetDto toDto(Meet meet);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "team.id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Meet updateMeet(MeetDto meetDto, @MappingTarget Meet meet);
}
