package faang.school.projectservice.controller.google_calendar;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.model.CalendarAclRole;
import faang.school.projectservice.model.CalendarAclScopeType;
import faang.school.projectservice.service.google_calendar.AclService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Google Calendar ACL", description = "Operations for managing access rights to Google Calendars")
@RequiredArgsConstructor
@RequestMapping("/google-calendar/acl")
@Slf4j
@RestController
public class AclController {
    private final AclService aclService;

    @Operation(summary = "Grant access to calendar",
            description = "Grants a user access to a Google Calendar with the specified role.")
    @PostMapping("/{calendarId}")
    public ResponseEntity<String> grantAccessToCalendar(
            @PathVariable String calendarId,
            @RequestParam String userEmail,
            @RequestParam CalendarAclRole role,
            @RequestParam CalendarAclScopeType scopeType) throws IOException {
        aclService.grantAccessToCalendar(calendarId, userEmail, role, scopeType);
        return ResponseEntity.ok("Access granted to user " + userEmail + " with role " + role);
    }

    @Operation(summary = "Get calendar ACL",
            description = "Retrieves access rights for a Google Calendar.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<List<AclRule>> getCalendarAcl(@PathVariable String calendarId) throws IOException {
        List<AclRule> aclRules = aclService.getCalendarAcl(calendarId);
        return ResponseEntity.ok(aclRules);
    }

    @Operation(summary = "Delete calendar ACL",
            description = "Deletes an access rule from a Google Calendar by its rule ID.")
    @DeleteMapping("/{calendarId}/{ruleId}")
    public ResponseEntity<String> deleteCalendarAcl(@PathVariable String calendarId, @PathVariable String ruleId) throws IOException {
        aclService.deleteCalendarAcl(calendarId, ruleId);
        return ResponseEntity.ok("Право доступа с ID " + ruleId + " удалено");
    }
}
