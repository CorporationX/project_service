package faang.school.projectservice.service.internShip;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internShip.InternshipDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final ScheduleRepository scheduleRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipDtoValidator validator;
    private final List<InternshipFilter> internshipFilters;

    public InternshipDto create(InternshipDto internshipDto) {
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        // Проверка, является ли ментор членом команды
        validator.validateMentorIsMember(mentor, project);

        Schedule schedule = getScheduleById(internshipDto.getScheduleId());

        List<TeamMember> interns = internshipDto.getInternIds().stream()
                .map(teamMemberRepository::findById).toList();

        Internship entity = internshipMapper.toEntity(internshipDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy(internshipDto.getCreatedBy()); // при создании, сразу же заполняем и обновление
        entity.setUpdatedBy(internshipDto.getCreatedBy());
        entity.setInterns(interns);
        entity.setProject(project);
        entity.setMentorId(mentor);
        entity.setSchedule(schedule);

        return internshipMapper.toDto(internshipRepository.save(entity));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        Internship entity = findInternshipById(internshipDto.getId());
        validator.checkCompletedStatus(internshipDto, entity);
        // Проверяю изменения
        checkChanges(entity, internshipDto);
        updateEntity(entity, internshipDto);
        return internshipMapper.toDto(internshipRepository.save(entity));
    }

    private void validateChangeMentor(Internship internship, InternshipDto internshipDto) {
        // Поменялся ли ментор
        if (!Objects.equals(internshipDto.getMentorId(), internship.getMentorId().getId())) {
            TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
            Project project = projectRepository.getProjectById(internshipDto.getProjectId());
            // Проверка, является ли ментор членом команды
            validator.validateMentorIsMember(mentor, project);
            internship.setMentorId(mentor); // временное хранилище для entity
        }
    }

    // Проверка изменений
    private void checkChanges(Internship internship, InternshipDto internShipDto) {
        validator.checkChanges(internship, internShipDto);
        // Проверяю не изменился ли ментор, и меняю его, если он состоит в проекте.
        validateChangeMentor(internship, internShipDto);
    }

    // Обновление полей, которые можно изменять
    private void updateEntity(Internship entity, InternshipDto dto) {
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setSchedule(getScheduleById(dto.getScheduleId()));
        entity.setEndDate(dto.getEndDate());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setStatus(dto.getStatus());
    }


    private Internship findInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new InternshipDtoValidateException("Стажировки с таким id не существует."));
    }

    private Schedule getScheduleById(long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new InternshipDtoValidateException("Такого расписания не существует."));
    }

    public InternshipDto getInternship(Long internshipId) {
        return internshipMapper.toDto(findInternshipById(internshipId));
    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }


    public List<InternshipDto> getFilteredInternship(InternshipFilterDto filters) {
        List<Internship> internships = internshipRepository.findAll();

        // Список подключенных фильтров
        List<InternshipFilter> actualFilters = internshipFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .toList();

        return internships.stream()
                .filter(e -> actualFilters.stream()
                        .allMatch(f -> f.apply(e, filters)))
                .map(internshipMapper::toDto)
                .toList();
    }
}
