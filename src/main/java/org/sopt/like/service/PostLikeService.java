package org.sopt.like.service;

import org.sopt.like.exception.LikeNotFoundException;
import org.sopt.like.domain.PostLike;
import org.sopt.like.repository.PostLikeRepository;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.domain.Post;
import org.sopt.post.exception.PostNotFoundException;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASS: 게시글 행 잠금 안에서 좋아요 추가,취소 처리
 */
@Service
public class PostLikeService {
    private final PostRepository posts; // 예: Spring이 주입한 PostRepository
    private final MemberRepository members; // 예: Spring이 주입한 MemberRepository
    private final PostLikeRepository likes; // 예: Spring이 주입한 PostLikeRepository

    /**
     * CONSTRUCTOR: 업무 처리에 필요한 저장소 또는 Service를 생성자로 주입
     */
    public PostLikeService(PostRepository posts, MemberRepository members, PostLikeRepository likes) {
        this.posts = posts;
        this.members = members;
        this.likes = likes;
    }

    /**
     * METHOD: 게시글을 잠그고 회원을 확인한 뒤 해당 좋아요만 추가
     */
    @Transactional
    public void like(Long postId, Long memberId) {
        Post post = posts.findForUpdate(postId).orElseThrow(PostNotFoundException::new);
        var member = members.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if (!likes.existsByPost_IdAndMember_Id(postId, memberId)) {
            likes.saveAndFlush(new PostLike(post, member));
        }
    }

    /**
     * METHOD: 게시글을 잠그고 회원을 확인한 뒤 해당 좋아요를 삭제
     * + 만약에 이전에 좋아요를 누르지 않은 member면 error404
     */
    @Transactional
    public void unlike(Long postId, Long memberId) {
        posts.findForUpdate(postId).orElseThrow(PostNotFoundException::new);
        members.findById(memberId).orElseThrow(MemberNotFoundException::new);

        if (!likes.existsByPost_IdAndMember_Id(postId, memberId)) {
            throw new LikeNotFoundException();
        }
        likes.deleteByPost_IdAndMember_Id(postId, memberId);
        likes.flush();
    }
}
