package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoForCreate;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubprojectMapper {

    @Mapping(target = "id", ignore = true)
    Project toEntity(SubprojectDtoForCreate dtoForCreate);
}
