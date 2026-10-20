package org.sopt.post.repository;

import jakarta.persistence.LockModeType;
import org.sopt.post.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sopt.post.domain.Category;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * INTERFACE: 저장 방식과 관계없이 게시글 저장소가 제공해야 하는 기능을 정의합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * METHOD:해당 회원이 참조되는 데이터가 존재하는지 확인
     */
    boolean existsByMember_Id(Long memberId);

    /**
     * METHOD: 페이지에 해당하는 게시글과 작성자를 함께 조회
     */
    @Override
    @EntityGraph(attributePaths = "member")
    Page<Post> findAll(Pageable pagenable);

    /**
     * METHOD: 선택한 카테고리의 게시글과 작성자를 페이지 단위로 조회
     */
    @EntityGraph(attributePaths = "member")
    Page<Post> findByCategory(Category category, Pageable pagenable);

    /**
     * METHOD: 게시글 행에 쓰기 잠금을 설정해 조회하고 트랜잭션 종료까지 잠금을 유지
     * 상세 조회 중 다른 트랜잭션이 같은 글의 조회수를 덮어쓰는 일을 막기 위해 행 잠금을 사용한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findForUpdate(@Param("id") Long id);
}
