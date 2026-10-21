package org.sopt.like.repository;

import org.sopt.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    /**
     * METHOD: 같은 게시글과 회원의 좋아요 관계가 이미 존재하는지 확인
     */
    boolean existsByPost_IdAndMember_Id(Long postId, Long memberId);

    /**
     * METHOD: 해당 회원이 참조되는 데이터가 존재하는지 확인
     */
    boolean existsByMember_Id(Long memberId);

    /**
     * METHOD: 해당 게시글에 저장된 좋아요 관계의 개수를 반환
     */
    long countByPost_Id(Long postId);

    /**
     * METHOD: 해당 회원이 특정 게시글에 남긴 좋아요 관게를 삭제
     */
    void deleteByPost_IdAndMember_Id(Long postId, Long memberId);

    /**
     * METHOD: 해당 게시글에 연결된 좋아요 관계를 삭제
     */
    void deleteByPost_Id(Long postId);
}
