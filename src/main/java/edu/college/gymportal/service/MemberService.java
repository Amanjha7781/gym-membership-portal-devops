package edu.college.gymportal.service;

import edu.college.gymportal.model.Member;
import edu.college.gymportal.model.MembershipStatus;
import edu.college.gymportal.repository.MemberRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class MemberService {
    private final MemberRepository members;

    public MemberService(MemberRepository members) { this.members = members; }

    public Member create(Member member) { return members.save(member); }
    @Transactional(readOnly = true)
    public List<Member> findAll() { return members.findAll(Sort.by(Sort.Direction.DESC, "id")); }
    @Transactional(readOnly = true)
    public Member findById(long id) { return members.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Member not found")); }
    @Transactional(readOnly = true)
    public List<Member> search(String term) { return term == null || term.isBlank() ? findAll() : members.search(term.trim()); }
    public Member update(long id, Member values) {
        Member current = findById(id);
        current.updateFrom(values);
        return members.save(current);
    }
    public Member changeStatus(long id, MembershipStatus status) {
        Member member = findById(id);
        member.changeStatus(status);
        return members.save(member);
    }
    @Transactional(readOnly = true)
    public long count() { return members.count(); }
    @Transactional(readOnly = true)
    public long countByStatus(MembershipStatus status) { return members.countByStatus(status); }
}

