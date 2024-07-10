package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentServiceValidator momentServiceValidator;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;

    public void saveMoment(MomentDto momentDto) {
        momentServiceValidator.validateCreateMoment(momentDto);
        CompletableFuture<Void> futureMoment = addMembersIDsToTheMoment(momentDto);

        futureMoment.thenRun(() -> momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    public List<MomentDto> getAllMoments(MomentFilterDto momentFilterDto) {
        List<Moment> moments = momentRepository.findAll();

        return momentMapper.toDto(momentFilters.stream()
                .filter(filter -> filter.isApplicable(momentFilterDto))
                .flatMap(filter -> filter.apply(moments, momentFilterDto))
                .toList());
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.toDto(momentRepository.findAll());
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id).orElse(null);
        momentServiceValidator.validateGetMomentById(moment);

        return momentMapper.toDto(moment);
    }

    public CompletableFuture<Void> addMembersIDsToTheMoment(MomentDto momentDto) {
        CopyOnWriteArrayList<Long> members = new CopyOnWriteArrayList<>(momentDto.getUserIDs());
        if (!members.isEmpty()) {
            members.clear();
        }

        List<Project> projects = momentDto.getProjectsIDs().stream()
                .map(projectRepository::getProjectById)
                .toList();

        return CompletableFuture.runAsync(() -> {
            List<Long> tempMembers = new ArrayList<>();
            projects.stream()
                    .forEach(project -> project.getTeams()
                            .forEach(team -> team.getTeamMembers()
                                    .forEach(teamMember -> tempMembers.add(teamMember.getId()))));
            members.addAll(tempMembers);
        }).thenRun(() -> momentDto.setUserIDs(members));
    }
}
