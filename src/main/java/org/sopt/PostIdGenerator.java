package org.sopt;

import org.springframework.stereotype.Component;

// 심화: 저장소에서 key로 사용할 고유 ID 발급을 담당합니다.
@Component
public class PostIdGenerator {
    private long nextId = 1L;

    public long generate() {
        return nextId++;
    }
}
