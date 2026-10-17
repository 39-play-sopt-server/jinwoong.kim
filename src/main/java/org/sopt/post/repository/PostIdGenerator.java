package org.sopt.post.repository;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong; //심화: 여러 스레드가 접근해도 값 반환과 증가를 하나의 나눌 수 없는 연산으로 처리

/**
 * CLASS: 메모리 저장소에서 사용할 게시글 ID를 순차적으로 발급합니다.
 */
@Component
public class PostIdGenerator {
    private final AtomicLong nextId = new AtomicLong(1L); // 예: 1L 발급 후 다음 값은 2L

    /**
     * METHOD: 현재 ID 반환과 다음 ID로의 증가를 하나의 원자적 연산으로 처리합니다.
     */
    public long generate() {
        return nextId.getAndIncrement();
    }
}
