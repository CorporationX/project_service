package faang.school.projectservice.service;
import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.CampaignCreateDto;
import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.mapper.CampaignCreateMapper;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.util.CurrencyConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final CampaignCreateMapper campaignCreateMapper;
    private final CampaignMapper campaignMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final DonationRepository donationRepository;

    @Transactional
    public CampaignDto createCampaign(CampaignCreateDto campaignCreateDto, Long userId) {
        validateUserToManipulateCampaign(userId, campaignCreateDto.getProjectId());
        campaignRepository.findByTitleAndProjectId(campaignCreateDto.getTitle(), campaignCreateDto.getProjectId())
                .ifPresent(campaign -> { throw new RuntimeException("Campaign already exists"); });
        var campaign = campaignCreateMapper.toEntity(campaignCreateDto);
        campaign.setCreatedBy(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setAmountRaised(new BigDecimal(0));
        campaign.setUpdatedBy(userId);
        campaign.setProject(projectService.getProjectById(campaignCreateDto.getProjectId()));
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDto donateToCampaign(Long campaignId, PaymentRequest paymentRequest, long userId) {
        var campaign = getById(campaignId);
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new RuntimeException("Campaign is not active");
        }
        var amount = CurrencyConverter.convert(paymentRequest.amount(),
                paymentRequest.currency(),
                campaign.getCurrency());

        var request = new PaymentRequest(new Random().nextLong(1000, 9000), amount,
                paymentRequest.currency());
        var paymentResponse = paymentServiceClient.sendPayment(request);
        if (!paymentResponse.status().equals("SUCCESS")) {
            throw new RuntimeException("Payment failed");
        }

        campaign.setAmountRaised(campaign.getAmountRaised().add(paymentResponse.amount()));
        if (campaign.getAmountRaised().compareTo(campaign.getGoal()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }
        var donation = Donation.builder()
                .amount(paymentResponse.amount())
                .donationTime(LocalDateTime.now())
                .paymentNumber(paymentResponse.paymentNumber())
                .userId(userId)
                .campaign(campaign)
                .build();

        donationRepository.save(donation);
        campaign.setUpdatedBy(userId);

        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public CampaignDto updateCampaign(Long campaignId, CampaignCreateDto campaignCreateDto, Long userId) {
        var campaign = getById(campaignId);
        validateUserToManipulateCampaign(userId, campaign.getProject().getId());
        if (!campaignCreateDto.getProjectId().equals(campaign.getProject().getId())) {
            throw new RuntimeException("Project cannot be changed");
        }
        BeanUtils.copyProperties(campaign, campaignCreateDto);
        campaign.setUpdatedBy(userId);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(Long campaignId, Long userId) {
        var campaign = getById(campaignId);
        validateUserToManipulateCampaign(userId, campaign.getProject().getId());

        campaignRepository.delete(campaign);
    }

    public CampaignDto getCampaign(Long id) {
        return campaignMapper.toDto(getById(id));
    }

    public List<CampaignDto> getAllCampaigns(CampaignFilterDto filterDto) {
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getPageSize());
        var statusName = isNull(filterDto.getStatus()) ? null : filterDto.getStatus().name();
        var campaigns = campaignRepository.findAllByFilters(filterDto.getNamePattern(), filterDto.getMinGoal(),
                filterDto.getMaxGoal(), statusName, pageable);

        return campaignMapper.toDto(campaigns);
    }

    public CampaignDto cancelCampaign(Long campaignId, Long userId) {
        var campaign = getById(campaignId);
        validateUserToManipulateCampaign(userId, campaign.getProject().getId());
        campaign.setStatus(CampaignStatus.CANCELED);

        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    private void validateUserToManipulateCampaign(Long userId, Long projectId) {
        var teamMember = teamMemberService.getTeamMember(userId, projectId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER) || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new RuntimeException("Only owner or manager can update campaign");
        }
    }

    private Campaign getById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
    }
}
