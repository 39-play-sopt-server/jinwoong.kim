package org.sopt.post.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sopt.like.domain.PostLike;
import org.sopt.like.repository.PostLikeRepository;
import org.sopt.like.service.PostLikeService;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PostLikeConcurrencyTest {

    @Autowired
    private PostLikeService service;

    @Autowired
    private MemberRepository members;

    @Autowired
    private PostRepository posts;

    @Autowired
    private PostLikeRepository likes;

    @Autowired
    private JdbcTemplate jdbc;

    private Long memberId; // 예: 테스트에서 생성한 회원 ID
    private Long postId; // 예: 테스트에서 생성한 개시글 ID

    /**
     * METHOD: 동시 요청에 사용할 회원과 게시글을 DB에 저장
     */
    @BeforeEach
    void setUp() {
        assertEquals("assignment_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
        Member member = members.saveAndFlush(new Member("동시성 테스트 회원"));
        memberId = member.getId();

        Post post = posts.saveAndFlush(
                new Post("테스트 제목", "테스트 본문", Category.GENERAL, member)
        );
        postId = post.getId();
    }

    /**
     * METHOD: 이 테스트가 만든 데이터만 외래 키 순서에 맞춰 삭제
     */
    @AfterEach
    void cleanUp() {
        if (postId != null) {
            jdbc.update("DELETE FROM post_like WHERE post_id = ?", postId);
            jdbc.update("DELETE FROM post WHERE id = ?", postId);

        }

        if (memberId != null) {
            jdbc.update("DELETE FROM member WHERE id = ?", memberId);
        }
    }

    /**
     * METHOD: 같은 회원의 동시 좋아요 10회가 한 행만 생성하는지 확인
     */
    @Test
    void concurrentLikesCreateOnlyOneRow() throws Exception {
        int requestCount = 10;

        // 모든 작업이 출발 준비를 마쳤는지 확인
        CountDownLatch ready = new CountDownLatch(requestCount);

        // 출발 신호 전까지 작업 대기
        CountDownLatch start = new CountDownLatch(1);

        List<Future<Void>> tasks = new ArrayList<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(requestCount)) {
            try{
                for (int i = 0; i < requestCount;i ++) {
                    Future<Void> task = executor.submit(() -> {
                        ready.countDown();
                        start.await();

                        service.like(postId, memberId);
                        return null;
                    });
                    tasks.add(task);
                }

                //모든 작업이 준비되면 한꺼번에 테스트 시작
                assertTrue(ready.await(10, TimeUnit.SECONDS));
                start.countDown();

                //모든 작업이 완료되면 발생한 오류 확인
                for (Future<Void> task:tasks) {
                    task.get(30, TimeUnit.SECONDS);
                }
            } finally {
                //중간에 실패하더라도 출발 대기 중인 작업을 풀어줌
                start.countDown();
            }
        }

        long likeCount = likes.countByPost_Id(postId);

        assertEquals(1L, likeCount);
        assertTrue(likes.existsByPost_IdAndMember_Id(postId, memberId));
    }
}
