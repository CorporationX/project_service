package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeamMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamMembers", ignore = true)
  @Mapping(target = "avatarKey", ignore = true)
  @Mapping(target = "project.id", source = "projectId")
  Team toEntity(TeamEvent teamEvent);
}
