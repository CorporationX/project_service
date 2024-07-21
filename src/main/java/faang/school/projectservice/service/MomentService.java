package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final TeamMemberRepository teamMemberRepository;

    public MomentDto createMoment(MomentDto momentDto) {
        momentDto.setDate(LocalDateTime.now());
        Moment moment = mapper.toEntity(momentDto);
        moment.setCreatedAt(LocalDateTime.now());
        momentRepository.save(moment);
        return mapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto, Long momentId) {
        Moment oldMoment = getMomentById(momentId);
        Moment newMoment = mapper.toEntity(momentDto);
        List<Project> oldProjects = oldMoment.getProjects();
        List<Project> newProjects = newMoment.getProjects();



    }

    private Moment getMomentById(Long momentId) {
        return momentRepository.findById(momentId).orElseThrow(() -> new DataValidationException("the moment with id " + momentId + " does not exist"));
    }

    private void addMemberAndProjectToMomentDto(TeamMember member, MomentDto momentDto) {
        Long userId = member.getUserId();
        if (!momentDto.getUserIds().contains(userId)) {momentDto.getUserIds().add(userId);}
        Long newProjectId = member.getTeam().getProject().getId();
        if (momentDto.getProjectIds().contains(newProjectId)) {momentDto.getProjectIds().add(newProjectId);}
    }

    private void addUserIdAndProjectToMomentDto(Long userId, MomentDto momentDto) {
        TeamMember newMember = teamMemberRepository.findById(userId);
        if (!momentDto.getUserIds().contains(newMember.getUserId())) {momentDto.getUserIds().add(newMember.getUserId());}
        Long newProjectId = newMember.getTeam().getProject().getId();
        if (momentDto.getProjectIds().contains(newProjectId)) {momentDto.getProjectIds().add(newProjectId);}
    }

}
