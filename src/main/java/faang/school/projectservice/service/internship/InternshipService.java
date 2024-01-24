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
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Period;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if(!checkInternshipDto(internshipDto))
            throw new IllegalArgumentException("Internship cannot be created");
        Internship createdInternship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toInternshipDto(createdInternship);
    }

    private boolean checkInternshipDto(InternshipDto internshipDto){
        // в этих методах уже пробрасываются исключения, поэтому при создании объекта тоже может вылететь исключение,
        // если данные не валидные, так? Их же не надо проверять?
        Project project = projectRepository.getProjectById(internshipDto.getProject().getId());
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId().getId());
        List<TeamMember> interns = internshipDto.getInterns();
        if (interns == null || interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
        //Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        Period period = Period.between(internshipDto.getStartDate().toLocalDate(), internshipDto.getEndDate().toLocalDate());
        if(period.getMonths() > 3)
            throw new IllegalArgumentException("Internship duration cannot exceed 90 days");
        return true;
    }
}
