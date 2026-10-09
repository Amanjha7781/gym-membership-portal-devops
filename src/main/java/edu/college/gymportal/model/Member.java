package edu.college.gymportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

@Entity
@Table(name = "members", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String name;

    @NotBlank @Email @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank @Pattern(regexp = "^[+0-9() .-]{7,20}$", message = "Enter a valid phone number")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipPlan plan;

    @NotNull @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipStatus status;

    protected Member() { }

    public Member(String name, String email, String phone, MembershipPlan plan,
                  LocalDate startDate, LocalDate endDate, MembershipStatus status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public MembershipPlan getPlan() { return plan; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public MembershipStatus getStatus() { return status; }

    @AssertTrue(message = "End date must be on or after start date")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPlan(MembershipPlan plan) { this.plan = plan; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public void updateFrom(Member values) {
        name = values.name;
        email = values.email;
        phone = values.phone;
        plan = values.plan;
        startDate = values.startDate;
        endDate = values.endDate;
        status = values.status;
    }

    public void changeStatus(MembershipStatus status) { this.status = status; }
}
