package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sopt.post.domain.Category;

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

}
