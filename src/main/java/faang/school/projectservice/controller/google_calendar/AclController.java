package faang.school.projectservice.controller.google_calendar;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.model.CalendarAclRole;
import faang.school.projectservice.model.CalendarAclScopeType;
import faang.school.projectservice.service.google_calendar.AclService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Google Calendar ACL", description = "Операции для управления правами доступа к календарям Google Calendar")
@RestController
@RequestMapping("/google-calendar/acl")
@Slf4j
public class AclController {

    private final AclService aclService;

    @Autowired
    public AclController(AclService aclService) {
        this.aclService = aclService;
    }

    @Operation(summary = "Предоставить доступ к календарю", description = "Предоставляет пользователю доступ к календарю Google Calendar с указанной ролью.")
    @PostMapping("/{calendarId}")
    public ResponseEntity<String> grantAccessToCalendar(
            @PathVariable String calendarId,
            @RequestParam String userEmail,
            @RequestParam CalendarAclRole role,
            @RequestParam CalendarAclScopeType scopeType) {
        log.info("Запрос на предоставление доступа к календарю '{}' для пользователя '{}'", calendarId, userEmail);
        aclService.grantAccessToCalendar(calendarId, userEmail, role, scopeType);
        return ResponseEntity.ok("Доступ предоставлен пользователю " + userEmail + " с ролью " + role);
    }

    @Operation(summary = "Получить права доступа к календарю", description = "Получает права доступа к календарю Google Calendar.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<List<AclRule>> getCalendarAcl(@PathVariable String calendarId) {
        log.info("Запрос на получение прав доступа к календарю '{}'", calendarId);
        List<AclRule> aclRules = aclService.getCalendarAcl(calendarId);
        return ResponseEntity.ok(aclRules);
    }

    @Operation(summary = "Удалить право доступа к календарю", description = "Удаляет право доступа к календарю Google Calendar по идентификатору правила доступа.")
    @DeleteMapping("/{calendarId}/{ruleId}")
    public ResponseEntity<String> deleteCalendarAcl(@PathVariable String calendarId, @PathVariable String ruleId) {
        log.info("Запрос на удаление права доступа с ruleId '{}' для календаря '{}'", ruleId, calendarId);
        aclService.deleteCalendarAcl(calendarId, ruleId);
        return ResponseEntity.ok("Право доступа с ID " + ruleId + " удалено");
    }
}
