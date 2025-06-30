package faang.school.projectservice.mapper.cover;

import faang.school.projectservice.dto.cover.CoverDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectCoverMapper {

    CoverDto toDto(Project project);

    default String extractFilename(String coverImageId) {
        if (coverImageId == null) return null;
        return coverImageId.substring(coverImageId.indexOf(" - ") + 3);
    }
}