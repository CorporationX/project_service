package faang.school.projectservice.controller;

import faang.school.projectservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaign")
public class CampaignController {
    private final CampaignService campaignService;
}
