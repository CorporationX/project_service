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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentServiceValidator momentServiceValidator;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;

    public void createMoment(MomentDto momentDto) {
        momentServiceValidator.validateSaveMoment(momentDto);
        List<Long> members = momentDto.getUserIDs();
        List<Project> projects = momentDto.getProjectsIDs().stream().map(projectRepository::getProjectById).toList();

        projects.stream()
                .forEach(project -> project.getTeams()
                        .forEach(team -> team.getTeamMembers()
                                .forEach(teamMember -> members.add(teamMember.getId()))));

        momentRepository.save(momentMapper.toEntity(momentDto));
    }


    public void updateMoment(MomentDto momentDto) {
        momentServiceValidator.validateUpdateMoment(momentDto);


        momentRepository.save(momentMapper.toEntity(momentDto));
    }

    public List<MomentDto> getAllMoments(MomentFilterDto momentFilterDto) {
        List<Moment> moments = momentRepository.findAll();

       return momentMapper.toDto(momentFilters.stream()
                .filter(filter -> filter.isApplicable(momentFilterDto))
                .reduce(moments , (currentMoments, filter) -> filter.apply(currentMoments, momentFilterDto), (a, b) -> b));
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.toDto(momentRepository.findAll());
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id).orElse(null);
        momentServiceValidator.validateGetMomentById(moment);

        return momentMapper.toDto(moment);
    }
}
