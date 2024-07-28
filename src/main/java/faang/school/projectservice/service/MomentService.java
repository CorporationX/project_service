package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final MomentValidator momentValidator;
    private final List<MomentFilter> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        processMoment(moment);
        momentRepository.save(moment);
        return mapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment momentToUpdate = momentRepository.findById(momentDto.getId()).orElseThrow(() -> new DataValidationException("No such moment"));
        mapper.update(momentDto, momentToUpdate);
        processMoment(momentToUpdate);
        momentRepository.save(momentToUpdate);
        return mapper.toDto(momentToUpdate);
    }

    public List<MomentDto> getMomentsFilteredByDateFromProjects(Long ProjectId, MomentFilterDto filters) {
        List<Moment> moments = momentRepository.findAllByProjectId(ProjectId);
        if (moments.isEmpty()) {
            throw new DataValidationException("zero moments with such id");
        }
        return momentFilters.stream().filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(moments, filters))
                .map(mapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments(Long projectId) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);
        if (moments.isEmpty()) {
            throw new DataValidationException("zero moments with such id");
        }
        return moments.stream().map(mapper::toDto).toList();
    }

    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow(() -> new DataValidationException("No moment with such id" + momentId));
        return mapper.toDto(moment);
    }

    private void processMoment(Moment moment) {
        List<Project> processedProjects = new ArrayList<>();
        for (Project project : moment.getProjects()) {
            Project processedProject = projectRepository.getProjectById(project.getId());
            momentValidator.validateProject(processedProject);
            processedProjects.add(processedProject);
        }
        List<TeamMember> members = new ArrayList<>();
        for (TeamMember member : moment.getMembers()) {
            TeamMember realMember = teamMemberRepository.findById(member.getId());
            members.add(realMember);
        }
        moment.setMembers(members);
        moment.setProjects(processedProjects);
    }

}
