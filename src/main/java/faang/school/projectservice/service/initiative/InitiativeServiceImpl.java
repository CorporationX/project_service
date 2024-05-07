package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.mapper.InitiativeMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitiativeServiceImpl implements InitiativeService {
    private final InitiativeMapper mapper;
    private final InitiativeValidator validator;
    private final InitiativeRepository initiativeRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;

    @Override
    public InitiativeDto create(InitiativeDto initiative) {
        validator.validate(initiative);

        Project project = projectRepository.getProjectById(initiative.getProjectId());
        TeamMember curator = teamMemberRepository.findById(initiative.getCuratorId());

        validator.validateCuratorAndProject(curator, project);

        List<Stage> stages = stageRepository.findAll().stream()
                .filter(stage -> initiative.getStageIds().contains(stage.getStageId()))
                .toList();

        Initiative entity = mapper.toEntity(initiative, project, curator, stages);
        Initiative saved = initiativeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public InitiativeDto update(InitiativeDto initiative) {
        return null;
    }

    @Override
    public List<InitiativeDto> getAllByFilter(InitiativeFilterDto filter) {
        return List.of();
    }

    @Override
    public List<InitiativeDto> getAll(InitiativeDto initiative) {
        return List.of();
    }

    @Override
    public InitiativeDto getById(long id) {
        return null;
    }
}
