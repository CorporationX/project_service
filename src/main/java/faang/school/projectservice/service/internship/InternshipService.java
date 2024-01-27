package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
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

   @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkExistenceInterns(internshipDto);
        checkInternshipDtoDate(internshipDto);
        Internship createdInternship = internshipMapper.toEntity(internshipDto);
        createdInternship.setProject(checkProject(internshipDto.getProjectId()));
        createdInternship.setMentorId(checkMentor(internshipDto.getMentorId()));
        createdInternship.setInterns(internshipDto.getInterns().stream()
                .map(teamMemberRepository::findById)
                .toList());
        Internship internshipNew = internshipRepository.save(createdInternship);
        return internshipMapper.toInternshipDto(internshipNew);
    }

    private Project checkProject(Long id) {
        return projectRepository.getProjectById(id);
    }

    private TeamMember checkMentor(Long id) {
        return teamMemberRepository.findById(id);
    }
    private void checkExistenceInterns(InternshipDto internshipDto) {
        List<Long> interns = internshipDto.getInterns();
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