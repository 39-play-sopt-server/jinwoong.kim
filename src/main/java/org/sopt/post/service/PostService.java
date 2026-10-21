package org.sopt.post.service;

import org.sopt.post.dto.PostPageResponse;
import org.sopt.post.repository.PostSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.sopt.member.domain.Member;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.exception.PostNotFoundException;
import org.sopt.post.repository.PostRepository;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CLASS: 게시글 조회·수정·삭제의 업무 흐름과 페이지네이션을 처리합니다.
 */
@Service
public class PostService {
    private final PostRepository repo; // 예: Spring이 주입한 MemoryPostRepository 객체
    private final MemberRepository members; // 예: Spring이 주입한 MemberRepository

    /**
     * CONSTRUCTOR: 저장 방식에 의존하지 않도록 PostRepository 인터페이스를 주입받습니다.
     */
    public PostService(PostRepository repo, MemberRepository members) {
        this.repo = repo;
        this.members = members;
    }

    /**
     * METHOD: ID 발급과 게시글 생성을 저장소에 위임합니다.
     */
    @Transactional
    public void createPost(
            String title,
            String content,
            Category category,
            Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("올바른 작성자 ID가 필요합니다.");
        }
        Member member = members.findById(memberId).orElseThrow(MemberNotFoundException::new);
        // members 도메인에서 id 가져오기

        Post post = new Post(title, content, category, member);
        repo.save(post); // 생성자로 개시글을 만들고 save로 db에저장됨
    }

    /**
     * METHOD: 게시글이 없으면 예외를 발생시키고, 있으면 조회수를 증가시킨 뒤 응답 DTO로 반환합니다.
     */
    @Transactional
    public PostResponse readPost(long id) {
        Post post = repo.findForUpdate(id).orElseThrow(PostNotFoundException::new);
        post.increaseViewCount();
        return PostResponse.from(post);

    }

    /**
     * METHOD: 입력을 검증하고 생성일 ID 내림추산으로 게시글을 DB페이지 조회합니다 + 동적 검색으로 여러 조건을 필터링합니다.
     * 범위를 벗어난 페이지는 빈 목록을 반환하며, 조회수는 증가시키지 않습니다.
     * Category 로 페이지 목록을 구분지어 읽어옵니다.
     */
    @Transactional(readOnly = true)
    public PostPageResponse readPostList(int page, int size, Category cat, Long memberId, String title) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("페이지는 0 이상, 사이즈는 1~100입니다.");
        }
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );
        if (memberId != null && memberId <= 0) {
            throw new IllegalArgumentException("작성자 ID는 양수여야 합니다.");
        }

        Page<Post> result = repo.findAll(PostSpecifications.filter(memberId, cat, title), pageable);

        return PostPageResponse.from(result.map(PostResponse::from));
    }
    /**
     * METHOD: 게시글이 없으면 예외를 발생시키고, 있으면 객체의 검증 규칙에 따라 수정합니다.
     */
    @Transactional
    public void updatePost(
            long id,
            String newTitle,
            String newContent
    ) {
        Post post = repo.findById(id).orElseThrow(PostNotFoundException::new);
        post.update(newTitle, newContent);
    }

    /**
     * METHOD: 저장소의 삭제 결과를 확인하고, 삭제할 게시글이 없으면 예외를 발생시킵니다.
     */
    @Transactional //실습: 조회와 삭제를 하나의 트랜잭션으로
    public void deletePost(long id) {
        Post post = repo.findById(id).orElseThrow(PostNotFoundException::new);

        repo.delete(post);
    }
}
