package org.sopt.post.service;

import jakarta.transaction.Transactional;
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
        Post post = repo.findById(id).orElseThrow(PostNotFoundException::new);
        post.increaseViewCount();
        return PostResponse.from(post);

    }

    /**
     * METHOD: 0부터 시작하는 페이지 번호와 페이지 크기를 검증하고, 해당 구간을 응답 DTO 목록으로 반환합니다.
     * 범위를 벗어난 페이지는 빈 목록을 반환하며, 조회수는 증가시키지 않습니다.
     * Category 로 페이지 목록을 구분지어 읽어옵니다.
     */
    public List<PostResponse> readPostList(int page, int size, Category category) {
        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }

        // int 곱셈의 범위 초과를 피하고, 앞 페이지에 해당하는 게시글 수를 계산합니다.
        long offset = (long) page * size;

        return repo.findAll().stream()
                .filter(post -> category == null || post.getCategory() == category)
                .skip(offset)
                .limit(size)
                .map(PostResponse::from)
                .toList();
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
