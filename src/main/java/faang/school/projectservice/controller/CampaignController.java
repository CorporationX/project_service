package faang.school.projectservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    @PostMapping
    public void createCampaign() {

    }

    @PutMapping("/{campaignId}")
    public void updateCampaign() {

    }

    @DeleteMapping("/{campaignId}")
    public void deleteCampaign() {

    }

    @GetMapping("/{campaignId}")
    public void getCampaignById() {

    }

    @GetMapping
    public void getFilteredCampaignsByProject() {

    }
}
