package org.sopt;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

@Repository
public class MemoryPostRepository implements PostRepository {
    // 심화: ID를 key로 사용하는 HashMap 저장소ㄴ
    private final Map<Long, Post> posts = new TreeMap<>();

    // 심화 METHOD: ID를 key로 게시글 저장
    @Override
    public void save(Post post) {
        posts.put(post.getId(), post); // 심화: ID 기반 저장소
    }

    // 심화 METHOD: ID로 조회하고 없는 게시글은 예외 처리
    @Override
    public Post findById(long id) {
        Post post = posts.get(id);

        if (post == null) {
            throw new PostNotFoundException();
        }

        return post;
    }

    // 심화 METHOD: ID로 삭제하고 없는 게시글은 예외 처리
    @Override
    public void deleteById(long id) {
       Post removed =  posts.remove(id);

       if (removed == null) {
           throw new PostNotFoundException();
       }
    }

    // 심화 METHOD: TreeMap의 값을 ID 순으로 정렬해 목록 반환
    // 별도 List를 반환해 목록 변경이 저장소 구조에 영향을 주지 않게 합니다.
    // List의 Post 객체는 저장소의 객체와 공유합니다.
    @Override
    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

}
