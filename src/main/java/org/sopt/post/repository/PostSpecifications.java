package org.sopt.post.repository;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;

/**
 * CLASS: 전달된 작성자 ID,카테고리,제목 조건만 동적 검색 조건으로 조합
 */
public final class PostSpecifications {
    /**
     * CONSTRUCTOR: 검색 조건 유틸리티의 인스턴스 생성 막음
     */
    private PostSpecifications() {}

    /**
     * METHOD: 이력된 조건만 Criteria조건으로 만들고 AND로 연결
     */
    public static Specification<Post> filter(Long memberId, Category cat, String title) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (memberId != null) {
                predicates.add(cb.equal(root.get("member").get("id"), memberId));
            }
            if (cat != null) {
                predicates.add(cb.equal(root.get("category"), cat));
            }
            if (title != null && !title.isBlank()) {
                String escaped = title.trim().replace("!", "!!")
                        .replace("%", "!%").replace("_", "!_");
                predicates.add(cb.like(root.get("title"), "%" + escaped + "%", '!'));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
