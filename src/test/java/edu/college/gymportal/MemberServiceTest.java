package edu.college.gymportal;

import static org.junit.jupiter.api.Assertions.*;

import edu.college.gymportal.model.*;
import edu.college.gymportal.repository.MemberRepository;
import edu.college.gymportal.service.MemberService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(MemberService.class)
class MemberServiceTest {
    @Autowired MemberService service;
    @Autowired MemberRepository repository;

    @Test void createAndUpdatePersistMembershipFields() {
        var member = new Member("Nisha Rao", "nisha@example.test", "9876543210", MembershipPlan.QUARTERLY,
                LocalDate.of(2026, 10, 1), LocalDate.of(2027, 1, 1), MembershipStatus.ACTIVE);
        Member saved = service.create(member);
        assertNotNull(saved.getId());
        var revised = new Member("Nisha Rao", "nisha@example.test", "9876543211", MembershipPlan.ANNUAL,
                LocalDate.of(2026, 10, 1), LocalDate.of(2027, 10, 1), MembershipStatus.ACTIVE);
        service.update(saved.getId(), revised);
        assertEquals(MembershipPlan.ANNUAL, repository.findById(saved.getId()).orElseThrow().getPlan());
        assertEquals("9876543211", repository.findById(saved.getId()).orElseThrow().getPhone());
    }

    @Test void statusCountsReflectPersistedMembershipState() {
        service.create(new Member("Kabir Sen", "kabir@example.test", "9876543212", MembershipPlan.MONTHLY,
                LocalDate.now(), LocalDate.now().plusMonths(1), MembershipStatus.ACTIVE));
        assertEquals(1, service.countByStatus(MembershipStatus.ACTIVE));
        assertEquals(1, service.count());
    }
}

