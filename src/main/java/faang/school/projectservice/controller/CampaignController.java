package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping("/create")
    public CampaignDto create(@Valid @RequestBody CampaignDto campaignDto) {
        return campaignService.create(campaignDto);
    }
}
