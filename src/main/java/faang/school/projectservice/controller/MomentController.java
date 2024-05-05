package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping("/create")
    public MomentDto create(@RequestBody @Valid MomentDto momentDto){
        return momentService.create(momentDto);
    }

    @PostMapping("/update")
    public MomentDto update(@RequestBody @Valid MomentDto momentDto){
        return momentService.update(momentDto);
    }

    @PostMapping("/filter")
    public List<MomentDto> filter(@RequestBody @Valid MomentFilterDto momentFilterDto){
        return momentService.filter(momentFilterDto);
    }

    @GetMapping("/get")
    public List<MomentDto> getAll(){
        return momentService.getAll();
    }

    @GetMapping("/get/{momentId}")
    public MomentDto get(@PathVariable long momentId){
        return momentService.get(momentId);
    }
}
