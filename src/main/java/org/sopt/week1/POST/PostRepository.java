package org.sopt.week1.POST;

import java.util.ArrayList;
import java.util.List;

// 심화: ID 기반 저장소
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PostRepository {
    // 심화: ID를 key로 사용하는 HashMap 저장소
    private final Map<Long, Post> posts = new HashMap<>();

    // 심화 METHOD: ID를 key로 게시글 저장
    public void save(Post post) {
        posts.put(post.getId(), post); // 심화: ID 기반 저장소
    }

    // 심화 METHOD: ID로 조회하고 없는 게시글은 예외 처리
    public Post findById(long id) {
        Post post = posts.get(id);

        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        return post;
    }

    // 심화 METHOD: ID로 삭제하고 없는 게시글은 예외 처리
    public void deleteById(long id) {
       Post removed =  posts.remove(id);

       if (removed == null) {
           throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
       }
    }

    // 심화 METHOD: HashMap의 값을 ID 순으로 정렬해 목록 반환
    // 별도 List를 반환해 목록 변경이 저장소 구조에 영향을 주지 않게 합니다.
    // List의 Post 객체는 저장소의 객체와 공유합니다.
    public List<Post> findAll() {
        List<Post> result = new ArrayList<>(posts.values());
        result.sort(Comparator.comparingLong(Post::getId));
        return result;
    }

}
