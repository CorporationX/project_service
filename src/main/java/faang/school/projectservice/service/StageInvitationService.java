package faang.school.projectservice.service;

import faang.school.projectservice.dto.FilterInvitationDto;
import faang.school.projectservice.dto.RejectionDto;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInvitationFilter<StageInvitation,StageInvitationDto>> stageInvitationFilters;

    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        StageInvitation savedInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(savedInvitation);
    }

    public StageInvitationDto acceptInvitation(Long id) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(id);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation updatedInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDto rejectInvitation(Long id, RejectionDto rejectionReason) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(id);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setRejectionReason(rejectionReason.getReason());
        StageInvitation updatedInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDto> getUserInvitations(Long userId, FilterInvitationDto filters) {
        Stream<StageInvitation> invitations = stageInvitationRepository.findAll().stream();
        List<Stream<StageInvitation>> filteredStreams = stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .map(filter -> filter.apply(invitations, filters))
                .toList();
        Stream<StageInvitation> combinedStream = Stream.concat(
                filteredStreams.get(0),
                filteredStreams.subList(1, filteredStreams.size()).stream().reduce(Stream.empty(), Stream::concat)
        );
        return invitations.map(stageInvitationMapper::toDto).toList();
    }
}
