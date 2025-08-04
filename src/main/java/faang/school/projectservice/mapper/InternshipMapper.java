package faang.school.projectservice.mapper;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

    default Long map(TeamMember member) {
        return member == null ? null : member.getId();
    }

    default TeamMember map(Integer id) {
        if (id == null) return null;
        TeamMember member = new TeamMember();
        member.setId(Long.valueOf(id));
        return member;
    }
}