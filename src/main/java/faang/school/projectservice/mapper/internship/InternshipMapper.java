package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;

import java.util.List;

public interface InternshipMapper {

    static Internship toInternship(CreateInternshipDto createInternshipDto,
                                   Project project,
                                   TeamMember mentor,
                                   List<TeamMember> interns) {
        return Internship.builder()
                .name(createInternshipDto.name())
                .description(createInternshipDto.description())
                .status(createInternshipDto.status())
                .role(createInternshipDto.role())
                .startDate(createInternshipDto.startDate())
                .endDate(createInternshipDto.endDate())
                .project(project)
                .mentorId(mentor)
                .interns(interns)
                .build();
    }

    static void update(UpdateInternshipDto dto, Internship internship) {
        if (dto == null) {
            return;
        }
        if (dto.name() != null) {
            internship.setName(dto.name());
        }
        if (dto.description() != null) {
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