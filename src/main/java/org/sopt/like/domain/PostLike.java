package org.sopt.like.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;
import org.sopt.member.domain.Member;
import org.sopt.post.domain.Post;

/**
 * CLASS: 회원과 게시글의 좋아요 관계를 저장하고 회원별 중복 좋아요를 제한
 */
@Entity
@Table(name = "post_like", uniqueConstraints = @UniqueConstraint(
        name = "uk_post_like_post_member", columnNames = {"post_id", "member_id"}))
public class PostLike extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1L; 저장 전에는 null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 예: 좋아요 대상 Post 객체

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 예: DB에서 조회한 좋아요 회원 Member 객체

    /**
     * CONSTRUCTOR: JPA가 좋아요 객체를 생성할 수 있도록 기본 생성자를 제공
     */
    protected PostLike() {}

    /**
     * CONSTRUCTOR: 좋아요 대상 게시글과 좋아요를 누른 회원을 연결
     */
    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }
}
