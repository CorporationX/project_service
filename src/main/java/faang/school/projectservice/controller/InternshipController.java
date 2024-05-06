package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validation.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {

    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;


    //ПРовести валидацию если надо. Проверить что поступаемые данные не пустые.
    //Контроллер принимает и возвращает InternshipDto или их коллекции

    //Создать стажировку.
    //Стажировка ВСЕГДА относится к какому-то одному проекту.
    // Создать стажировку можно только в том случае, если есть кого стажировать.
    // При создании нужно проверить, что стажировка длится не больше 3 месяцев, и что у стажирующихся есть ментор из команды проекта.
    @PostMapping
    public InternshipDto create(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateInternshipDto(internshipDto);
        return internshipService.create(internshipDto);
    }

    //Обновить стажировку
    @PutMapping("/{id}")
    public InternshipDto update(@RequestBody InternshipDto internshipDto, @PathVariable long id) {
        internshipValidator.validateInternshipDto(internshipDto);
        return internshipService.update(internshipDto, id);
    }

    //Получить все стажировки проекта с фильтрами по статусу или роли
    @GetMapping("/filter")
    public List<InternshipDto> findAllWithFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findAllWithFilter(internshipFilterDto);
    }

    //получить все стажировки
    @GetMapping
    public List<InternshipDto> findAll() {
        return internshipService.findAll();
    }

    //получить стажировку по id
    @GetMapping("/{id}")
    public InternshipDto findById(@PathVariable long id) {
        return internshipService.findById(id);
    }
}
