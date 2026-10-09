package edu.college.gymportal.controller;

import edu.college.gymportal.model.Member;
import edu.college.gymportal.model.MembershipPlan;
import edu.college.gymportal.model.MembershipStatus;
import edu.college.gymportal.service.MemberService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PortalController {
    private final MemberService service;
    public PortalController(MemberService service) { this.service = service; }

    @GetMapping("/login") public String login() { return "login"; }

    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("total", service.count());
        model.addAttribute("active", service.countByStatus(MembershipStatus.ACTIVE));
        model.addAttribute("inactive", service.countByStatus(MembershipStatus.INACTIVE));
        model.addAttribute("expired", service.countByStatus(MembershipStatus.EXPIRED));
        model.addAttribute("members", service.search(search));
        model.addAttribute("statuses", MembershipStatus.values());
        model.addAttribute("search", search == null ? "" : search);
        return "index";
    }

    @GetMapping("/members/new")
    public String newMember(Model model) {
        model.addAttribute("member", new Member("", "", "", MembershipPlan.MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1), MembershipStatus.ACTIVE));
        model.addAttribute("plans", MembershipPlan.values());
        model.addAttribute("statuses", MembershipStatus.values());
        model.addAttribute("pageTitle", "Register member");
        model.addAttribute("formAction", "/members");
        return "member-form";
    }

    @PostMapping("/members")
    public String create(@Valid @ModelAttribute Member member, BindingResult errors, Model model, RedirectAttributes flash, Authentication authentication) {
        if (errors.hasErrors()) { prepareForm(model, "Register member", "/members"); return "member-form"; }
        if (!isAdmin(authentication)) member.setStatus(MembershipStatus.ACTIVE);
        service.create(member);
        flash.addFlashAttribute("notice", "Member registered successfully.");
        return "redirect:/";
    }

    @GetMapping("/members/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("member", service.findById(id));
        prepareForm(model, "Update member", "/members/" + id);
        return "member-form";
    }

    @PostMapping("/members/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute Member member, BindingResult errors, Model model, RedirectAttributes flash, Authentication authentication) {
        if (errors.hasErrors()) { prepareForm(model, "Update member", "/members/" + id); return "member-form"; }
        if (!isAdmin(authentication)) member.setStatus(service.findById(id).getStatus());
        service.update(id, member);
        flash.addFlashAttribute("notice", "Member details updated.");
        return "redirect:/";
    }

    @PostMapping("/members/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeStatus(@PathVariable long id, @RequestParam MembershipStatus status, RedirectAttributes flash) {
        service.changeStatus(id, status);
        flash.addFlashAttribute("notice", "Membership status updated.");
        return "redirect:/";
    }

    private void prepareForm(Model model, String title, String action) {
        model.addAttribute("plans", MembershipPlan.values());
        model.addAttribute("statuses", MembershipStatus.values());
        model.addAttribute("pageTitle", title);
        model.addAttribute("formAction", action);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
