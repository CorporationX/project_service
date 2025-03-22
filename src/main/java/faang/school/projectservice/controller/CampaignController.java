package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CampaignCreateDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public CampaignUpdateDto create(@Valid @RequestBody CampaignCreateDto campaignCreateDto) {
        return campaignService.create(campaignCreateDto);
    }

    @PostMapping("/update")
    public CampaignUpdateDto update(@Valid @RequestBody CampaignUpdateDto campaignUpdateDto) {
        return campaignService.update(campaignUpdateDto);
    }

    @DeleteMapping("/{id}")
    public CampaignUpdateDto delete(@PathVariable long id) {
        return campaignService.delete(id);
    }
}
