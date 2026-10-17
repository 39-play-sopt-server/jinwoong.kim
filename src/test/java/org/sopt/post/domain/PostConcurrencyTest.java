package org.sopt.post.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CLASS: 여러 스레드가 같은 게시글을 조회할 때 조회수 누락 여부를 검증합니다.
 */
class PostConcurrencyTest {

    /**
     * METHOD: 10개 스레드가 각각 1,000번 증가시키면 조회수가 10,000인지 확인합니다.
     */
    @Test
    void concurrentViewsPreserveEveryIncrement() throws Exception {
        Post post = new Post("제목", "본문", Category.GENERAL, 1L);

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Future<?>> tasks = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                tasks.add(executor.submit(() -> {
                    for (int j = 0; j < 1_000; j++) {
                        post.increaseViewCount();
                    }
                }));
            }

            for (Future<?> task : tasks) {
                task.get();
            }
        }

        assertEquals(10_000L, post.getViewCount());
    }
}