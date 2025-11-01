package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

@Component
public class InternshipMapper {

    public static Internship toInternship(CreateInternshipDto createInternshipDto,
                                   Project project,
                                   TeamMember mentor) {
        return Internship.builder()
                .name(createInternshipDto.name())
                .description(createInternshipDto.description())
                .status(createInternshipDto.status())
                .role(createInternshipDto.role())
                .startDate(createInternshipDto.startDate())
                .endDate(createInternshipDto.endDate())
                .project(project)
                .mentorId(mentor)
                .build();
    }
}