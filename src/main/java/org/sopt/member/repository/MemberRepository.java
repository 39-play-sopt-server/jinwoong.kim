package org.sopt.member.repository;
import org.sopt.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * INTERFACE: 회원 엔티티의 저장,조회,삭제 기능을 JPA 저장소에 위임합니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * METHOD: 이메일로 회원을 조회하고 존재하지 않으면 빈 Optional을 반환합니다.
     */
    Optional<Member> findByEmail(String email);

    /**
     * METHOD: 회원가입 전에 같은 이메일을 사용하는 회원이 있는지 확인합니다.
     */

    /**
     * METHOD: 회원의 토큰 버전을 증가시켜 기존 토큰을 무효화
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Member m
            SET m.tokenVersion = m.tokenVersion + 1
            WHERE m.id = :memberId
            """)
    int increamentTokenVersion(@Param("memberId") Long memberId);

    boolean existsByEmail(String email);
}
