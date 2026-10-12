package org.sopt.post.repository;

import org.springframework.stereotype.Component;

/**
 * CLASS: 메모리 저장소에서 사용할 게시글 ID를 순차적으로 발급합니다.
 */
@Component
public class PostIdGenerator {
    private long nextId = 1L; // 예: 1L 발급 후 다음 값은 2L

    /**
     * METHOD: 다음 ID를 반환하고 발급 번호를 1 증가시킵니다.
     */
    public long generate() {
        return nextId++;
    }
}
