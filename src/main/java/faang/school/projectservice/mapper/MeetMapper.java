package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.meet.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "status", target = "status", defaultValue = "ACTIVE")
    @Mapping(source = "teamId", target = "team.id")
    Meet toEntity(MeetDto meetDto);


    @Mapping(source = "team.id", target = "teamId")
    MeetDto toDto(Meet meet);
}
