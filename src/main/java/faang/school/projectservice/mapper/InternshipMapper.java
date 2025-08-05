package faang.school.projectservice.mapper;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * InternshipMapper — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author agent
 * @since 04.08.2025
 */
@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "traineeIds", ignore = true)
    @Mapping(target = "mentorId", source = "mentorId.id")
    InternshipDto toDto(Internship entity);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "mentorId.id", source = "mentorId")
    Internship toEntity(InternshipDto dto);

    List<InternshipDto> toDtoList(List<Internship> entities);

    InternshipStatus map(faang.school.projectservice.model. InternshipStatus status);

    faang.school.projectservice.model.InternshipStatus map(InternshipStatus status);
}

