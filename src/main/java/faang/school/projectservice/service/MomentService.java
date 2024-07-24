package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final TeamMemberRepository teamMemberRepository;
    private final MomentValidator momentValidator;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        moment.setCreatedAt(LocalDateTime.now());
        deleteDuplicateProjects(moment);
        momentRepository.save(moment);
        return mapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment momentToUpdate = momentRepository.findById(momentDto.getId()).orElseThrow(() -> new DataValidationException("No such moment"));
        mapper.update(momentDto, momentToUpdate);
        momentToUpdate.setUpdatedAt(LocalDateTime.now());
        addProjectsAndUserIdsToMoment(momentToUpdate);
        addProjectsAndMembersToMoment(momentToUpdate);
        deleteDuplicateProjects(momentToUpdate);
        momentRepository.save(momentToUpdate);
        return mapper.toDto(momentToUpdate);
    }

    private void deleteDuplicateProjects(Moment moment) {
        Set<Project> uniqueProjects = new HashSet<>(moment.getProjects());
        List<Project> cleanedProjects = new ArrayList<>(uniqueProjects);
        moment.setProjects(cleanedProjects);
    }

    private void addMemberAndProjectToMoment(TeamMember member, Moment moment) {
        Long userId = member.getUserId();
        if (!moment.getUserIds().contains(userId)) {
            moment.getUserIds().add(userId);
        }
        momentValidator.validateProject(member.getTeam().getProject());
        Project newProject = member.getTeam().getProject();
        if (moment.getProjects().contains(newProject)) {
            moment.getProjects().add(newProject);
        }
    }

    private void addProjectsAndMembersToMoment(Moment moment) {
        for (TeamMember member : moment.getMembers()) {
            addMemberAndProjectToMoment(member, moment);
        }
    }

    private void addUserIdAndProjectToMoment(Long userId, Moment moment) {
        TeamMember newMember = teamMemberRepository.findById(userId);
        if (!moment.getUserIds().contains(newMember.getUserId())) {
            moment.getUserIds().add(newMember.getUserId());
        }
        momentValidator.validateProject(newMember.getTeam().getProject());
        Project newProject = newMember.getTeam().getProject();
        if (moment.getProjects().contains(newProject)) {
            moment.getProjects().add(newProject);
        }
    }

    private void addProjectsAndUserIdsToMoment(Moment moment) {
        for (Long userId : moment.getUserIds()) {
            addUserIdAndProjectToMoment(userId, moment);
        }
    }

}
