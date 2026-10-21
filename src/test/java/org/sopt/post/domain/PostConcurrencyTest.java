package org.sopt.post.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS: 실제 MySQL과 Service 트랜잭션에서 게시글 조회수와 작성자 응답을 검증합니다.
 */
@SpringBootTest
@ActiveProfiles("test")
class PostConcurrencyTest {
    @Autowired
    private PostService service;
    @Autowired
    private MemberRepository members;
    @Autowired
    private PostRepository posts;
    @Autowired
    private JdbcTemplate jdbc;

    private Long memberId; // 예: 이 테스트가 생성한 회원 ID
    private Long postId; // 예: 이 테스트가 생성한 게시글 ID

    /**
     * METHOD: 테스트 전용 DB인지 확인하고 별도 트랜잭션으로 회원과 글을 저장합니다.
     */
    @BeforeEach
    void setUp() {
        assertEquals("assignment_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
        Member member = members.saveAndFlush(new Member("조회수 테스트 회원"));
        memberId = member.getId();
        Post post = posts.saveAndFlush(new Post("조회수 테스트", "테스트 본문", Category.GENERAL, member));
        postId = post.getId();
    }

    /**
     * METHOD: 이 테스트가 만든 데이터만 좋아요·게시글·회원 순으로 삭제합니다.
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
     * METHOD: 10개 작업이 5번씩 상세 조회해도 조회수 50회가 누락되지 않는지 검증합니다.
     */
    @Test
    void concurrentViewsPreserveEveryIncrement() throws Exception {
        int workerCount = 10;
        int readsPerWorker = 5;
        int expectedCount = workerCount * readsPerWorker;
        CountDownLatch ready = new CountDownLatch(workerCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<List<Long>>> tasks = new ArrayList<>();
        Set<Long> returnedCounts = new HashSet<>();

        // 테스트에 @Transactional을 붙이지 않습니다. 각 Service 호출이 별도 트랜잭션입니다.
        try (ExecutorService executor = Executors.newFixedThreadPool(workerCount)) {
            try {
                for (int i = 0; i < workerCount; i++) {
                    tasks.add(executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        List<Long> counts = new ArrayList<>();
                        for (int read = 0; read < readsPerWorker; read++) {
                            counts.add(service.readPost(postId).viewCount());
                        }
                        return counts;
                    }));
                }
                assertTrue(ready.await(10, TimeUnit.SECONDS), "모든 작업이 출발 준비를 마쳐야 합니다.");
                start.countDown();
                for (Future<List<Long>> task : tasks) {
                    returnedCounts.addAll(task.get(30, TimeUnit.SECONDS));
                }
            } finally {
                start.countDown();
            }
        }

        // Service로 다시 조회하면 +1되므로 Repository에서 최종 저장값을 읽습니다.
        Post stored = posts.findById(postId).orElseThrow();
        assertEquals(expectedCount, stored.getViewCount());
        assertEquals(expectedCount, returnedCounts.size(), "각 응답의 조회수도 서로 달라야 합니다.");
        for (long count = 1; count <= expectedCount; count++) {
            assertTrue(returnedCounts.contains(count), "누락된 조회수: " + count);
        }
    }

    /**
     * METHOD: DB에 저장된 생성일과 작성자 이름이 상세 응답에 포함되는지 검증합니다.
     */
    @Test
    void detailIncludesAuthorAndCreationTime() {
        PostResponse response = service.readPost(postId);
        assertEquals(memberId, response.memberId());
        assertEquals("조회수 테스트 회원", response.authorName());
        assertNotNull(response.createdAt());
        assertEquals(1L, response.viewCount());
        assertEquals(0L, response.likeCount());
    }

    /**
     * METHOD: 목록 조회는 조회수를 변경하지 않고, 상세 조회만 증가시키는지 검증합니다.
     */
    @Test
    void listDoesNotIncreaseViewCount() {
        var page = service.readPostList(0, 5, Category.GENERAL, memberId, "조회수 테스트");
        assertEquals(1L, page.totalElements());
        assertEquals(0L, page.content().getFirst().viewCount());
        assertEquals(0L, posts.findById(postId).orElseThrow().getViewCount());

        service.readPost(postId);
        assertEquals(1L, posts.findById(postId).orElseThrow().getViewCount());
    }
}
