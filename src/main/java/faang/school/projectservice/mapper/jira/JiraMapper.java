package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.CreateJiraDto;
import faang.school.projectservice.dto.jira.ResponseJiraDto;
import faang.school.projectservice.model.Jira;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JiraMapper {
    JiraMapper INSTANCE = Mappers.getMapper(JiraMapper.class);

    @Mapping(target = "project.id", source = "projectId")
    Jira createDtoToEntity(CreateJiraDto dto);

    @Mapping(target = "projectId", source = "project.id")
    ResponseJiraDto entityToResponseDto(Jira jira);

    List<ResponseJiraDto> entityListToDtoList(List<Jira> jiras);
}