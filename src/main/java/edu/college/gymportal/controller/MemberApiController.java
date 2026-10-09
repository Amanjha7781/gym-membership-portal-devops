package edu.college.gymportal.controller;

import edu.college.gymportal.model.Member;
import edu.college.gymportal.model.MembershipStatus;
import edu.college.gymportal.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {
    private final MemberService service;
    public MemberApiController(MemberService service) { this.service = service; }
    @GetMapping public List<Member> all(@RequestParam(required = false) String search) { return service.search(search); }
    @GetMapping("/search") public List<Member> search(@RequestParam String term) { return service.search(term); }
    @GetMapping("/{id}") public Member one(@PathVariable long id) { return service.findById(id); }
    @PostMapping public Member create(@Valid @RequestBody Member member, Authentication authentication) {
        if (!isAdmin(authentication)) member.setStatus(MembershipStatus.ACTIVE);
        return service.create(member);
    }
    @PutMapping("/{id}") public Member update(@PathVariable long id, @Valid @RequestBody Member member, Authentication authentication) {
        if (!isAdmin(authentication)) member.setStatus(service.findById(id).getStatus());
        return service.update(id, member);
    }
    @PutMapping("/{id}/status") public Member status(@PathVariable long id, @RequestParam MembershipStatus value) { return service.changeStatus(id, value); }
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
