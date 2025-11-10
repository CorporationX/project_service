package faang.school.projectservice.controller.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.service.campaign.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignDto> create(@RequestBody @Valid CreateCampaignDto createCampaignDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(createCampaignDto));
    }

    @PatchMapping("/{campaignId}")
    public ResponseEntity<CampaignDto> update(@PathVariable Long campaignId,
                                              @RequestBody @Valid UpdateCampaignDto updateCampaignDto) {
        return ResponseEntity.ok(campaignService.update(campaignId, updateCampaignDto));
    }

    @DeleteMapping("/{campaignId}")
    public void softDelete(@PathVariable Long campaignId) {
        campaignService.softDelete(campaignId);
    }

    @GetMapping
    public ResponseEntity<List<CampaignDto>> getByFilters(@ModelAttribute CampaignFilterDto campaignFilterDto) {
        return ResponseEntity.ok(campaignService.getByFilters(campaignFilterDto));
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignDto> getById(@PathVariable Long campaignId) {
        return ResponseEntity.ok(campaignService.getCampaignById(campaignId));
    }
}