package edu.college.gymportal.repository;

import edu.college.gymportal.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where lower(m.name) like lower(concat('%', :term, '%')) " +
           "or lower(m.email) like lower(concat('%', :term, '%')) or cast(m.id as string) = :term")
    List<Member> search(@Param("term") String term);
    long countByStatus(edu.college.gymportal.model.MembershipStatus status);
}

