package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.validator.TeamValidator;
import faang.school.projectservice.validator.meet.MeetValidator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class MeetService {

    private final UserContext userContext;
    private final MeetMapper meetMapper;
    private final MeetValidator meetValidator;
    private final TeamValidator teamValidator;
    private final MeetJpaRepository meetJpaRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<MeetFilter> meetFilters;

    @Transactional
    public MeetDto createMeet(MeetDto meetDto) {
        long userId = userContext.getUserId();
        Team team = teamValidator.verifyTeamExistence(meetDto.getTeamId());
        teamValidator.verifyUserExistenceInTeam(userId, team);
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setCreatedBy(userId);
        team.addMeet(meet);
        Meet savedMeet = meetJpaRepository.save(meet);
        return meetMapper.toDto(savedMeet);
    }

    @Transactional
    public MeetDto updateMeet(long meetId, MeetDto meetDto) {
        long userId = userContext.getUserId();
        Meet meet = meetValidator.verifyMeetExistence(meetId);
        meetValidator.verifyUserIsCreatorOfMeet(userId, meet);
        Meet updatedMeet = meetMapper.updateMeet(meetDto, meet);
        Meet savedMeet = meetJpaRepository.save(updatedMeet);
        return meetMapper.toDto(savedMeet);
    }

    @Transactional
    public void deleteMeet(long meetId) {
        long userId = userContext.getUserId();
        Meet meet = meetValidator.verifyMeetExistence(meetId);
        meetValidator.verifyUserIsCreatorOfMeet(userId, meet);
        meet.getTeam().removeMeet(meet);
        meetJpaRepository.delete(meet);
        log.info(String.format("Meet with ID: %d was deleted", meetId));
    }

    @Transactional(readOnly = true)
    public List<MeetDto> getFilteredMeetsOfTeam(long teamId, MeetFilterDto meetFilterDto) {
        long userId = userContext.getUserId();
        Team team = teamValidator.verifyTeamExistence(teamId);
        teamValidator.verifyUserExistenceInTeam(userId, team);
        Stream<Meet> meets = team.getMeets().stream();
        for (MeetFilter meetFilter : meetFilters) {
            meets = meetFilter.filter(meets, meetFilterDto);
        }
        return meets.map(meetMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MeetDto> getAllMeetsOfUser() {
        long userId = userContext.getUserId();
        List<TeamMember> teamMemberList = teamMemberJpaRepository.findByUserId(userId);
        Stream<Meet> allMeetsOfUser = teamMemberList.stream()
                .flatMap(teamMember -> teamMember.getTeam().getMeets().stream());
        return allMeetsOfUser.map(meetMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MeetDto getMeetById(long meetId) {
        long userId = userContext.getUserId();
        Meet meet = meetValidator.verifyMeetExistence(meetId);
        teamValidator.verifyUserExistenceInTeam(userId, meet.getTeam());
        return meetMapper.toDto(meet);
    }
}
