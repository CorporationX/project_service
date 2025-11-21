package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "internsIds", expression = "java(mapInternsToIds(internship.getInterns()))")
    InternshipDto toDto(Internship internship);

    @Named("mapInternsToIds")
    default List<Long> mapInternsToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).collect(Collectors.toList());
    }

    static Internship toEntity(InternshipCreateDto createInternshipDto,
                               Project project,
                               TeamMember mentor,
                               List<TeamMember> interns,
                               Long createdBy) {
        return Internship.builder()
                .name(createInternshipDto.name())
                .description(createInternshipDto.description())
                .status(InternshipStatus.IN_PROGRESS)
                .role(createInternshipDto.role())
                .startDate(createInternshipDto.startDate())
                .endDate(createInternshipDto.endDate())
                .project(project)
                .mentorId(mentor)
                .interns(interns)
                .createdBy(createdBy)
                .build();
    }

    static void update(InternshipUpdateDto dto, Internship internship) {
        if (dto == null) {
            return;
        }
        if (dto.name() != null && !dto.name().isBlank()) {
            internship.setName(dto.name());
        }
        if (dto.description() != null && !dto.description().isBlank()) {
            internship.setDescription(dto.description());
        }
        if (dto.status() != null) {
            internship.setStatus(dto.status());
        }
        if (dto.endDate() != null) {
            internship.setEndDate(dto.endDate());
        }
    }

}