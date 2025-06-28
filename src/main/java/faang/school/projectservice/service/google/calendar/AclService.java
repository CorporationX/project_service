package faang.school.projectservice.service.google.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.google.calendar.AclDto;
import faang.school.projectservice.exception.AclException;
import faang.school.projectservice.model.google.calendar.Role;
import faang.school.projectservice.model.google.calendar.ScopeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AclService {
    private final GoogleOAuth googleOAuth;

    private static final String CALENDAR_ID = "primary";

    public void grandAccess(AclDto acl)  {
        log.info("Granting access with ACL: {}", acl);

        try {
            Calendar service = googleOAuth.init();
            AclRule rule = new AclRule()
                    .setScope(new AclRule.Scope()
                            .setType(acl.getScopeType().toString())
                            .setValue(acl.getScopeValue()))
                    .setRole(acl.getRole().toString());

            service.acl().insert("primary", rule).execute();
        } catch (IOException e) {
            throw new RuntimeException("Error ACL adding", e);
        }
    }

    public void deleteAclRule(String ruleId) {
        try {
            Calendar service = googleOAuth.init();
            service.acl().delete(CALENDAR_ID, ruleId).execute();
        } catch (IOException e) {
            throw new AclException("Ошибка при удалении ACL");
        }
    }

    public List<AclDto> listAclRules() {
        try {
            Calendar service = googleOAuth.init();
            List<AclRule> rules = service.acl().list(CALENDAR_ID).execute().getItems();
            return rules.stream().map(this::mapToDto).toList();
        } catch (IOException e) {
            throw new AclException("Ошибка при получении ACL");
        }
    }

    private AclDto mapToDto(AclRule rule) {
        return AclDto.builder()
                .ruleId(rule.getId())
                .role(Role.valueOf(rule.getRole().toUpperCase()))
                .scopeType(ScopeType.valueOf(rule.getScope().getType().toUpperCase()))
                .scopeValue(rule.getScope().getValue())
                .build();
    }
}
