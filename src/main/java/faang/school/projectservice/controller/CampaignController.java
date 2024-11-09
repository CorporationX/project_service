package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.service.CampaignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Project Campaigns management", description = "Operations related to manipulations with raising funds for projects")
public class CampaignController {
    private final CampaignService campaignService;
    private final CampaignMapper mapper;

    @GetMapping("/campaigns/{campaignId}")
    public CampaignDto getCampaign(@PathVariable Long campaignId) {
        Campaign requested = campaignService.findCampaignById(campaignId);
        return mapper.toDto(requested);
    }

    @PostMapping("/projects/{projectId}/campaigns")
    public List<CampaignDto> getCampaignsForProject(@PathVariable Long projectId,
                                                    @RequestBody(required = false) CampaignFilterDto filter,
                                                    Pageable pageable) {
        List<Campaign> campaignsForProject = campaignService.findFilteredCampaigns(projectId, filter, pageable);
        return mapper.toDtoList(campaignsForProject);
    }

    @PostMapping("/campaigns")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto createCampaign(@RequestBody @Valid CampaignDto campaignDto) {
        Campaign toCreate = mapper.toEntity(campaignDto);
        Campaign created = campaignService.createNewCampaign(toCreate);
        return mapper.toDto(created);
    }

    @PutMapping("/campaigns/{campaignId}")
    public CampaignDto updateCampaignInfo(@PathVariable Long campaignId,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String description) {
        Campaign updated = campaignService.updateCampaignInfo(campaignId, title, description);
        return mapper.toDto(updated);
    }

    @PutMapping("/campaigns/{campaignId}/soft-delete")
    public CampaignDto markCampaignForDeletion(@PathVariable Long campaignId) {
        Campaign markedForDeletion = campaignService.softDeleteById(campaignId);
        return mapper.toDto(markedForDeletion);
    }
}
