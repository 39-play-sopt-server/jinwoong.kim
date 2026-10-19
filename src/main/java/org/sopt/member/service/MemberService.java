package org.sopt.member.service;

import org.springframework.transaction.annotation.Transactional;
import org.sopt.common.exception.BusinessException;
import org.sopt.common.exception.ErrorCode;
import org.sopt.member.domain.Member;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;

/**
 * CLASS: 회원 생성과 탈퇴 조건 검증 및 삭제의 업무 흐름을 처리
 */
@Service
@Transactional
public class MemberService {
    private final MemberRepository members; // 예: Spring이 주입한 MemberRepository
    private final PostRepository posts; // 예: Spring이 주입한 PostRepository

    /**
     * CONTRUCTOR: 업무 처리에 필요한 저장소 또는 Service를 생성자로 주입받음
     */
    public MemberService(MemberRepository members, PostRepository posts) {
        this.members = members;
        this.posts = posts;
    }

    /**
     * METHOD: 이름을 검증해 회원을 저장하고 저장된 회원 정보를 반환
     */
    public MemberResponse create(String name) {
        return MemberResponse.from(members.save(new Member(name)));
    }

    /**
     * METHOD: 회원 존재 여부와 작성글을 검사하고 삭제를 DB에 반영
     */
    public void delete(Long id) {
        Member m = members.findById(id).orElseThrow(MemberNotFoundException::new);
        if (posts.existsByMember_Id(id)) {
            throw new BusinessException(ErrorCode.MEMBER_HAS_POSTS);
        }
        members.delete(m);
        members.flush();
    }
}
