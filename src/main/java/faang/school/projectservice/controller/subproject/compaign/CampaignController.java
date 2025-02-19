package faang.school.projectservice.controller.subproject.compaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.service.campaign.CampaignService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    private final CampaignMapper campaignMapper;

    @PostMapping("/campaign")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto publishCampaign(@Valid @RequestBody CampaignDto campaignDto) {

        return campaignService.publishCampaign(campaignDto);
    }

    @PatchMapping("/campaign")
    public CampaignDto updateCampaign(@Valid @RequestBody CampaignUpdateDto updateDto) {
        return campaignService.updateCampaign(updateDto);
    }

    @PutMapping("/{id}")
    public void deleteCampaign(@PathVariable @Min(1L) long id) {

        campaignService.deleteCampaign(id);
    }

    @PostMapping("/{id}")
    public CampaignDto getCampaign(@PathVariable @Min(1L) Long id) {
        return campaignService.getCampaign(id);
    }

    @PostMapping("/get-by-project/{projectId}")
    public ResponseEntity<List<CampaignDto>> getFilteredCampaignsByProject(@PathVariable @Positive @NotNull Long projectId,
                                                                           @RequestBody CampaignFilterDto campaignFilterDto) {

        List<Campaign> campaignList = campaignService.getCampaignsByProjectIdAndFilter(projectId, campaignFilterDto);
        List<CampaignDto> campaignDtoList = campaignMapper.toDtoList(campaignList);
        return ResponseEntity.ok().body(campaignDtoList);
    }
}
