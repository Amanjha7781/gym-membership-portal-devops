package edu.college.gymportal;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.college.gymportal.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberPortalIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired MemberRepository repository;

    @BeforeEach void cleanDatabase() { repository.deleteAll(); }

    private String payload(String name, String email, String status) {
        return """
                {"name":"%s","email":"%s","phone":"+91 98765 43210","plan":"MONTHLY","startDate":"2026-10-01","endDate":"2026-11-01","status":"%s"}
                """.formatted(name, email, status);
    }

    @Test void memberCanBeCreatedRetrievedUpdatedAndSearched() throws Exception {
        mvc.perform(post("/api/members").with(user("staff").roles("STAFF")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload("Asha Verma", "asha@example.test", "ACTIVE")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());

        long id = repository.findAll().getFirst().getId();
        mvc.perform(get("/api/members/" + id).with(user("staff").roles("STAFF")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Asha Verma"));
        mvc.perform(get("/api/members").param("search", "Asha").with(user("staff").roles("STAFF")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].email").value("asha@example.test"));
        mvc.perform(put("/api/members/" + id).with(user("staff").roles("STAFF")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload("Asha Jha", "asha@example.test", "ACTIVE")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Asha Jha"));
    }

    @Test void onlyAdminCanChangeMembershipStatus() throws Exception {
        var member = repository.save(new edu.college.gymportal.model.Member("Ravi Kumar", "ravi@example.test", "9876543210",
                edu.college.gymportal.model.MembershipPlan.MONTHLY, java.time.LocalDate.now(), java.time.LocalDate.now().plusMonths(1),
                edu.college.gymportal.model.MembershipStatus.ACTIVE));
        mvc.perform(put("/api/members/" + member.getId() + "/status?value=INACTIVE").with(user("staff").roles("STAFF")).with(csrf()))
            .andExpect(status().isForbidden());
        mvc.perform(put("/api/members/" + member.getId() + "/status?value=INACTIVE").with(user("admin").roles("ADMIN")).with(csrf()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test void staffCannotSetStatusThroughCreateOrUpdatePayload() throws Exception {
        mvc.perform(post("/api/members").with(user("staff").roles("STAFF")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload("Nina Das", "nina@example.test", "EXPIRED")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACTIVE"));
        long id = repository.findAll().getFirst().getId();
        mvc.perform(put("/api/members/" + id).with(user("staff").roles("STAFF")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload("Nina Das", "nina@example.test", "EXPIRED")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test void validationRejectsInvalidMemberInput() throws Exception {
        mvc.perform(post("/api/members").with(user("staff").roles("STAFF")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(payload("", "not-an-email", "ACTIVE")))
            .andExpect(status().isBadRequest());
        org.junit.jupiter.api.Assertions.assertEquals(0L, repository.count());
    }

    @Test void unauthenticatedUsersAreSentToLogin() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }
}
