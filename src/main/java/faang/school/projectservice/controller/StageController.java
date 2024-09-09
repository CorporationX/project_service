package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageController {
    private final StageService service;
    public void create(StageDto stageDto) {
        service.create(stageDto);
    }
}
