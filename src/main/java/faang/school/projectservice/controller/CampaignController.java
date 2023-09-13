package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.company.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;
    private final UserContext userContext;

    @GetMapping("/all")
    public List<CampaignDto> getAllCampaignsWithFilters(@RequestParam(value = "status", required = false) String status,
                                                        @RequestParam(value = "authorId", required = false) Long authorId) {
        return campaignService.getAllCampaignsWithFilters(status, authorId);
    }

    @PostMapping("/publish")
    public CampaignDto publishCampaign(@RequestBody @Valid CampaignDto dto) {
        return campaignService.publishCampaign(dto, userContext.getUserId());
    }

    @PutMapping("/")
    public CampaignDto updateCampaign(@RequestBody @Valid CampaignDto dto) {
        return campaignService.updateCampaign(dto, userContext.getUserId());
    }

    @GetMapping("/{id}")
    public CampaignDto getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
    }
}
