package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipDto {

    private long id;
    @NotNull(message = "Project must not be null")
    @Valid
    private Project project;
    private TeamMember mentorId;
    private List<TeamMember> interns;
    @NotNull(message = "Status must not be null")
    private InternshipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static Internship dtoTo(InternshipDto internshipDto) {
        Internship internship = new Internship();
        internship.setId(internshipDto.getId());
        internship.setProject(internshipDto.getProject());
        internship.setMentorId(internshipDto.getMentorId());
        internship.setInterns(internshipDto.getInterns());
        internship.setStatus(internshipDto.getStatus());
        internship.setStartDate(internshipDto.getStartDate());
        internship.setEndDate(internshipDto.getEndDate());
        return internship;
    }

    public static InternshipDto toDto(Internship internship) {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(internship.getId());
        internshipDto.setProject(internship.getProject());
        internshipDto.setMentorId(internship.getMentorId());
        internshipDto.setInterns(internship.getInterns());
        internshipDto.setStatus(internship.getStatus());
        internshipDto.setStartDate(internship.getStartDate());
        return internshipDto;
    }
}
