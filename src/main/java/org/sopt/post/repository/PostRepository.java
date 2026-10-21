package org.sopt.post.repository;

import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NullMarked;
import org.sopt.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sopt.post.domain.Category;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * INTERFACE: 게시글 저장 조회와 작성자 존재 확인 및 DB페이지 조회 제공
 */
@NullMarked
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    /**
     * METHOD:해당 회원이 작성하 게시글이 있는지 확인
     */
    boolean existsByMember_Id(Long memberId);

    /**
     * METHOD: 게시글과 작성자를 함께 페이지 단위로 조회
     */
    @Override
    @EntityGraph(attributePaths = "member")
    Page<Post> findAll(Pageable pageable);

    /**
     * METHOD: 선택한 카테고리의 게시글과 작성자를 페이지 단위로 조회
     */
    @EntityGraph(attributePaths = "member")
    Page<Post> findByCategory(Category category, Pageable pageable);

    /**
     * METHOD: 게시글을 쓰기 잠금으로 조회하고 트랜잭션 종료까지 잠금 유지
     * f상세 조회 중 다른 트랜잭션이 같은 글의 조회수를 덮어쓰는 일을 막기 위해 행 잠금을 사용한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findForUpdate(@Param("id") Long id);

    /**
     * METHOD: 동적 조건에 맞는 게시글과 작성자를 페이지 단위로 조회
     */
    @Override
    @EntityGraph(attributePaths = "member")
    Page<Post> findAll(Specification<Post> spec, Pageable pageable);
}
