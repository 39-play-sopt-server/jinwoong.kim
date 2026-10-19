package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * INTERFACE: 저장 방식과 관계없이 게시글 저장소가 제공해야 하는 기능을 정의합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * METHOD:해당 회원이 참조되는 데이터가 존재하는지 확인
     */
    boolean existsByMember_Id(Long memberId);
}
