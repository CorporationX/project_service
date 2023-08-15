package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;
    @Lazy
    private final ProjectService projectService;

    public MomentDto createMoment(MomentDto momentDto) {
        momentValidator.checkIsProjectClosed(momentDto.getProjectIds());
        List<Long> userIds = projectService.getProjectsById(momentDto.getProjectIds()).stream()
                        .flatMap(project -> project.getTeams().stream())
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getUserId)
                        .toList();
        momentDto.setUserIds(userIds);
        momentDto.setDate(LocalDateTime.now());

        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);

        return momentMapper.toDto(moment);
    }
}
