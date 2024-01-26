package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkInternshipDtoValid(internshipDto);
        Internship createdInternship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toInternshipDto(createdInternship);
    }

    private void checkInternshipDtoValid(InternshipDto internshipDto) {
        checkInternshipDtoId(internshipDto);
        checkProject(internshipDto);
        checkMentor(internshipDto);
        checkExistenceInterns(internshipDto);
        checkInternshipDtoDate(internshipDto);
    }

    private void checkInternshipDtoId(InternshipDto internshipDto) {
        if (internshipRepository.existsById(internshipDto.getId()))
            throw new IllegalArgumentException("Internship with this id " + internshipDto.getId() + " already exists");
    }


    private void checkProject(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProject().getId());
        if (project == null)
            throw new IllegalArgumentException("Project with id " + internshipDto.getProject().getId() + " not found");
    }

    private void checkMentor(InternshipDto internshipDto) {
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId().getId());
        if (mentor == null)
            throw new IllegalArgumentException("Mentor with id " + internshipDto.getMentorId().getId() + " not found");
    }

    private void checkExistenceInterns(InternshipDto internshipDto) {
        List<TeamMember> interns = internshipDto.getInterns();
        if (interns == null || interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
    }

    private void checkInternshipDtoDate(InternshipDto internshipDto) {
        if (internshipDto.getStartDate() == null || internshipDto.getEndDate() == null)
            throw new NullPointerException("Invalid dates");
        if (internshipDto.getStartDate().isAfter(internshipDto.getEndDate()))
            throw new IllegalArgumentException("Incorrect dates have been entered");
        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (duration.toDays() > 91)
            throw new IllegalArgumentException("Internship duration cannot exceed 91 days");
    }


}
