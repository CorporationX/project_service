package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final CandidateRepository candidateRepository;


    //Сервис принимает и возвращает InternshipDto

    //Создать стажировку.
    //Стажировка ВСЕГДА относится к какому-то одному проекту.
    // Создать стажировку можно только в том случае, если есть кого стажировать.
    // При создании нужно проверить, что стажировка длится не больше 3 месяцев, и что у стажирующихся есть ментор из команды проекта.
    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {
        //Проверка что список кандидатов не пустой и что они существуют в БД.
        List<Candidate> candidates = internshipDto.getCandidateIds().stream()
                .map(id -> candidateRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException(String.format("Candidate doesn't exist by id: %s", id))))
                .toList();
        internshipValidator.validateCandidatesList(candidates.size());

        //Проверить что ментор существует и он пренадлежит команде проекта
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internshipValidator.validateMentorInTeamProject(mentor, project);

        //Проверить что стажировка длится не меньше 3-х месяцев.
        internshipValidator.validateInternshipPeriod(internshipDto);

        //Создать для каждого кандидата на стажировку TeamMember
        List<TeamMember> interns = createInterns(candidates, mentor.getTeam());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional
    private List<TeamMember> createInterns(List<Candidate> candidates, Team team) {
        return candidates.stream()
                .map(candidate -> {
                            TeamMember intern = TeamMember.builder()
                                    .userId(candidate.getUserId())
                                    .roles(Arrays.asList(TeamRole.INTERN))
                                    .team(team)
                                    .build();
                            return teamMemberRepository.create(intern);
                        }
                ).toList();
    }

    //Обновить стажировку
    //Если стажировка завершена, то стажирующиеся должны получить новые роли на проекте, если прошли,
    // и быть удалены из списка участников проекта, если не прошли.
    //Участник считается прошедшим стажировку, если все запланированные задачи выполнены.
    // После старта стажировки нельзя добавлять новых стажёров. Стажировку можно пройти досрочно или досрочно быть уволенным.
    @Transactional
    public InternshipDto update(InternshipDto internshipDto, long id) {
        return null;
    }

    public List<InternshipDto> findAll() {
        return internshipMapper.toListDto(internshipRepository.findAll());
    }

    public InternshipDto findById(long id) {
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Internship doesn't exist by id: %s", id))));
    }

    public List<InternshipDto> findAllWithFilter(InternshipFilterDto internshipFilterDto) {
        return null;
    }


}
