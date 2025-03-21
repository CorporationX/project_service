package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.exception.stage.DataValidException;
import faang.school.projectservice.mapper.StageCreateMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final StageCreateMapper stageCreateMapper;
    private final StageRolesMapper stageRolesMapper;
    private final StageMapper stageMapper;
    private final StageInvitationRepository stageInvitationRepository;

    @Autowired
    public StageService(StageRepository stageRepository, TeamMemberRepository teamMemberRepository,
                        ProjectRepository projectRepository, StageCreateMapper stageCreateMapper,
                        StageRolesMapper stageRolesMapper, StageMapper stageMapper, StageInvitationRepository stageInvitationRepository) {
        this.stageRepository = stageRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectRepository = projectRepository;
        this.stageCreateMapper = stageCreateMapper;
        this.stageRolesMapper = stageRolesMapper;
        this.stageMapper = stageMapper;
        this.stageInvitationRepository = stageInvitationRepository;
    }

    public StageDTO create(StageDtoCreate stageDtoCreate, Long creatorId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        TeamMember creator = teamMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember not found with ID: " + creatorId));
        if (!creator.getRoles().contains(TeamRole.OWNER) || !creator.getRoles().contains(TeamRole.MANAGER)){
            log.error("TeamMember roles not allowed to be OWNER or MANAGER");
            throw new DataValidException("Don`t have permission " + creator.getRoles());
        }
        if (project.getStatus().equals(ProjectStatus.COMPLETED)
                || project.getStatus().equals(ProjectStatus.CANCELLED)) {
            log.error("Project status is " + project.getStatus());
            throw new DataValidException("Project status is " + project.getStatus());
        }
        if (stageDtoCreate == null || creatorId == null || projectId == null) {
            log.error("Creator ID: {}, Project ID: {}, Stage DTO: {}", creatorId, projectId, stageDtoCreate);
            throw new DataValidException("Data not valid");
        }

        Stage stage = stageCreateMapper.toEntity(stageDtoCreate);
        List<StageRoles> stageRoles = stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        stage.setStageRoles(stageRoles);

        if (project.getStages() == null) {
            project.setStages(new ArrayList<>());
        }
        stage.setProject(project);
        project.getStages().add(stage);
        List<TeamMember> fundedExecutors = findAllExecutors(stageDtoCreate.getRoleAndCount(), stage, project);
        stage.setExecutors(fundedExecutors);
        sendInvite(stage,fundedExecutors,creator);
        stageRepository.save(stage);
        log.info("Created stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public StageDTO update(StageDTO stageDTO) {

        return null;
    }

    public List<StageDTO> getRoleAndStatus(StageFilterDTO stageFilterDTO) {
        List<Stage> filteredStages = stageRepository.findStagesByRolesAndTaskStatus
                (stageFilterDTO.getTeamRoles(),stageFilterDTO.getTasksStatus());
        return stageMapper.toDtoList(filteredStages);
    }

    public List<StageDTO> getAllProjectStages(@NotNull Long projectId) {
       Project project = projectRepository.findById(projectId)
               .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
    return stageMapper.toDtoList(project.getStages());
    }

    public StageDTO getStage(@NotNull Long stageId) {
    return stageMapper.toDto(stageRepository.findById(stageId)
            .orElseThrow(() -> new EntityNotFoundException("Stage not found with ID: " + stageId)));
    }

    public void delete(@NotNull Long stageId) {
        Stage deletedStage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found with ID: " + stageId));
        log.info("Deleted stage: {}", deletedStage);
        stageRepository.delete(deletedStage);
    }

    private List<TeamMember> findAllExecutors(HashMap<TeamRole, Integer> roleAndCount, Stage stage, Project project) {
        List<TeamMember> Executors = new ArrayList<>();
        Set<Long> invitedIds = new HashSet<>();

        Map<Long, List<TeamRole>> stageExecutors = getRoleAndIdFromStage(stage);

        Map<Long, List<TeamRole>> projectExecutors = getRoleAndIdFromProject(project);

        BiFunction<Map<Long, List<TeamRole>>, HashMap<TeamRole, Integer>, List<TeamMember>> findCandidates =
                (executors, remainingRoles) -> {
                    List<TeamMember> selected = new ArrayList<>();
                    Iterator<Map.Entry<TeamRole, Integer>> iterator = remainingRoles.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<TeamRole, Integer> entry = iterator.next();
                        TeamRole role = entry.getKey();
                        int count = entry.getValue();

                        List<Long> candidates = executors.entrySet().stream()
                                .filter(e -> e.getValue().contains(role)
                                        && !invitedIds.contains(e.getKey()))
                                .map(Map.Entry::getKey)
                                .limit(count)
                                .toList();

                        for (Long candidateId : candidates) {
                            invitedIds.add(candidateId);
                            selected.add(teamMemberRepository.findById(candidateId).orElseThrow(
                                            ()-> new EntityNotFoundException("candidate not found" + candidateId)));
                            remainingRoles.put(role, remainingRoles.get(role) - 1);
                        }
                        remainingRoles.entrySet().removeIf(e -> e.getValue() <= 0);
                    }
                    return selected;
                };
        Executors.addAll(findCandidates.apply(stageExecutors, roleAndCount));
        if (!roleAndCount.isEmpty()) {
            Executors.addAll(findCandidates.apply(projectExecutors, roleAndCount));
        }
        return Executors;
    }

    private Map<Long,List<TeamRole>> getRoleAndIdFromProject(Project project) {
      return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .collect(toMap(
                        TeamMember::getId,
                        TeamMember::getRoles));
    }

    private Map<Long,List<TeamRole>> getRoleAndIdFromStage(Stage stage) {
        return stage.getExecutors().stream()
                .collect(toMap(TeamMember::getId,
                        TeamMember::getRoles));

    }

    private void sendInvite(Stage stage, List<TeamMember> invitedMembers, TeamMember author) {
        invitedMembers.forEach(teamMember -> {
            StageInvitation stageInvitation = StageInvitation.builder()
                    .status(StageInvitationStatus.PENDING)
                    .stage(stage)
                    .author(author)
                    .invited(teamMember)
                    .build();
            log.info("Sending invite on stage to : {}", teamMember);
            stageInvitationRepository.save(stageInvitation);
        });
    }
}
