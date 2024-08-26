package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    public Moment createMoment(MomentDto momentDto) {
        return momentRepository.save(momentMapper.toEntity(momentDto));
    }

    public Moment getMoment(Project subProject) {
        List<TeamMember> teamMembers = subProject.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
        Moment moment = Moment.builder()
                .name("Выполнены все подпроекты")
                .projects(subProject.getChildren())
                .userIds(teamMembers
                        .stream()
                        .mapToLong(TeamMember::getId)
                        .boxed()
                        .toList())
                .build();
        return momentRepository.save(moment);
    }
}
