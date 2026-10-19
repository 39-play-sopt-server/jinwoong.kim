package org.sopt.post.dto;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * RECORD: 게시글 목록과 현재 페이지, 전체 개수, 다음페이지 여부를 전달
 */
public record PostPageResponse (
    List<PostResponse> content, // 예: 현재 페이지의 개시글 5개
    int page, // 예: 0 (첫페이지)
    int size, // 예: 요청한 페이지 크기 5
    long totalElements, // 예: 조건에 맞는 전체 게시글 12개
    int totalPages, // 예: 전체 3페이지
    boolean hasNext // 예 다음페이지가 있으면 :true
) {
    /**
     * METHOD: Page의 내용과 페이지 정보를 API응답 구조로 복사
     */
    public static PostPageResponse from(Page<PostResponse> result) {
        return new PostPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }
}
