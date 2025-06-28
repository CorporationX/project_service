package faang.school.projectservice.controller.google.calendar;

import com.ecwid.consul.v1.acl.model.Acl;
import faang.school.projectservice.dto.google.calendar.AclDto;
import faang.school.projectservice.service.google.calendar.AclService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/google/calendar/acls")
@RequiredArgsConstructor
public class AclController {
    private final AclService aclService;

    @PostMapping
    public ResponseEntity<String> grandAccess(@Valid @RequestBody AclDto acl) {
        log.info("Granting access with ACL: {}", acl);
        aclService.grandAccess(acl);
        return ResponseEntity.ok("Access added successfully");
    }

    @GetMapping
    public List<AclDto> getAcl() {
        log.info("Getting ACL");
        return aclService.listAclRules();
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<String> revokeAccess(@PathVariable String ruleId) {
        aclService.deleteAclRule(ruleId);
        return ResponseEntity.ok("Access revoked successfully");

    }
}
