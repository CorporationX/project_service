package faang.school.projectservice.vacancy.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.vacancy.dto.AddCandidatesDto;
import faang.school.projectservice.vacancy.dto.CloseVacancyDto;
import faang.school.projectservice.vacancy.dto.VacancyCreateDto;
import faang.school.projectservice.vacancy.dto.VacancyDto;
import faang.school.projectservice.vacancy.dto.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.vacancy.mapper.VacancyMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRoleService projectRoleService;
    private final UserContext userContext;
    private final VacancyMapper vacancyMapper;

    @Transactional
    public VacancyDto createVacancy(VacancyCreateDto dto) throws AccessDeniedException {
        long userId = userContext.getUserId();
        checkOwnerOrManager(userId, dto.getProjectId(), "создания вакансии");

        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setId(UUID.randomUUID());
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setCandidates(new ArrayList<>());

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyDto updateVacancy(UUID id, VacancyUpdateDto dto) throws AccessDeniedException {
        long userId = userContext.getUserId();
        Vacancy vacancy = getVacancyEntity(id);

        checkOwnerOrManager(userId, vacancy.getProject().getId(), "обновления вакансии");

        vacancy.setTitle(dto.getTitle());
        vacancy.setPosition(dto.getPosition());
        vacancy.setSlots(dto.getSlots());
        vacancy.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyDto addCandidates(UUID id, AddCandidatesDto dto) {
        Vacancy vacancy = getVacancyEntity(id);

        List<Candidate> candidatesToAdd = dto.getCandidates().stream()
                .map(vacancyMapper::toEntity)
                .filter(candidate -> !projectRoleService.isProjectMember(vacancy.getProject().getId(), candidate.getId()))
                .collect(Collectors.toList());

        vacancy.getCandidates().addAll(candidatesToAdd);
        vacancy.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyDto closeVacancy(UUID id, CloseVacancyDto dto) throws AccessDeniedException {
        long userId = userContext.getUserId();
        Vacancy vacancy = getVacancyEntity(id);

        checkOwnerOrManager(userId, vacancy.getProject().getId(), "закрытия вакансии");

        if (dto.getSelectedCandidateIds().size() != vacancy.getSlots()) {
            throw new IllegalStateException("Количество выбранных кандидатов должно совпадать с количеством мест.");
        }

        projectRoleService.assignRolesToProject(
                vacancy.getProject().getId(),
                vacancy.getPosition(),
                dto.getSelectedCandidateIds()
        );

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setUpdatedAt(LocalDateTime.now());

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional()
    public List<VacancyDto> getAllVacancies() {
        return vacancyRepository.findAll().stream()
                .map(vacancyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional()
    public VacancyDto getVacancyById(UUID id) {
        return vacancyMapper.toDto(getVacancyEntity(id));
    }

    private Vacancy getVacancyEntity(UUID id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вакансия не найдена."));
    }

    @Transactional()
    public List<Vacancy> filterVacancies(TeamRole position, String title) {
        return vacancyRepository.findByPositionAndTitleContainingIgnoreCase(position, title);
    }

    public VacancyDto getVacancy(UUID id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vacancy not found with id: " + id));
        return vacancyMapper.toDto(vacancy);
    }

    private void checkOwnerOrManager(long userId, UUID projectId, String action) throws AccessDeniedException {
        if (!projectRoleService.isOwnerOrManager(userId, projectId)) {
            throw new AccessDeniedException("Нет прав для " + action + ": требуется роль OWNER или MANAGER.");
        }
    }
}
