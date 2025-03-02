package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;

    public Internship createInternship(InternshipDto internshipDto) {
        Long projectId = internshipDto.getProjectId().getId();
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found");
        }

        if (internshipDto.getStartDate().isAfter(internshipDto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plusMonths(3))) {
            throw new IllegalArgumentException("Internship cannot last more than 3 months");
        }

        if (internshipDto.getInternIds().contains(internshipDto.getMentorId())) {
            throw new IllegalArgumentException("Interns cannot mentor themselves");
        }




//        Стажировка ВСЕГДА относится к какому-то одному проекту.
//        Создать стажировку можно только в том случае, если есть кого стажировать.
//        При создании нужно проверить, что стажировка длится не больше 3 месяцев, и что у стажирующихся есть ментор из команды проекта.

        log.info("Creating internship: {}", internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);

        return internshipRepository.save(internship);
    }
}
