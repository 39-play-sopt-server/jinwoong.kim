package org.sopt.member.repository;
import org.sopt.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * INTERFACE: 회원 엔티티의 저장,조회,삭제 기능을 JPA 저장소에 위임합니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {}
