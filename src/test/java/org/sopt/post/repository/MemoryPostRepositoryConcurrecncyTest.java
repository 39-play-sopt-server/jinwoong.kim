package org.sopt.post.repository;

import org.junit.jupiter.api.Test;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CLASS: 동시 생성 시 게시글 개수와 ID의 중복 여부를 검증합니다.
 */
class MemoryPostRepositoryConcurrencyTest {

    /**
     * METHOD: 10개 스레드가 각각 100개를 생성해 총 1,000개가 보존되는지 확인합니다.
     */
    @Test
    void concurrentCreatesPreserveAllPostsAndUniqueIds() throws Exception {
        MemoryPostRepository repo =
                new MemoryPostRepository(new PostIdGenerator());

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Future<?>> tasks = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                tasks.add(executor.submit(() -> {
                    for (int j = 0; j < 100; j++) {
                        repo.create("제목", "본문", Category.GENERAL);
                    }
                }));
            }

            for (Future<?> task : tasks) {
                task.get();
            }
        }

        List<Post> posts = repo.findAll();

        assertEquals(1_000, posts.size());
        assertEquals(
                1_000L,
                posts.stream().map(Post::getId).distinct().count()
        );
    }
}