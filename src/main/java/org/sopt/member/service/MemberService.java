package org.sopt.member.service;

import org.sopt.member.exception.MemberErrorCode;
import org.sopt.like.repository.PostLikeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.repository.RefreshTokenRepository;
import java.util.List;
import org.sopt.member.dto.MemberResponse;

/**
 * CLASS: 회원 탈퇴 조건 검증 및 삭제의 업무 흐름을 처리
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository members; // 예: Spring이 주입한 MemberRepository
    private final PostLikeRepository likes; // 예: 좋아요 존재 여부 확인 저장소
    private final PostRepository posts; // 예: Spring이 주입한 PostRepository
    private final RefreshTokenRepository refreshTokens; // 탈퇴 회원의 Refresh

    /**
     * METHOD: 회원 존재 여부와 작성글을 검사하고 삭제를 DB에 반영
     */
    public void delete(Long memberId) {
        // 1. 탈퇴할 회원이 존재하는지 확인
        Member member = members.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 2. 작성한 게시글이 남아 있으면 탈퇴를 거절
        if (posts.existsByMember_Id(memberId)) {
            throw new BusinessException(MemberErrorCode.MEMBER_HAS_POSTS);
        }

        // 3. 좋아요 기록이 남아 있으면 탈퇴를 거절
        if (likes.existsByMember_Id(memberId)) {
            throw new BusinessException(MemberErrorCode.MEMBER_HAS_LIKES);
        }

        // 4. 해당 회원의 Refresh Token 허용 기록을 모두 삭제
        refreshTokens.deleteByMemberId(memberId);

        // 5. 회원을 삭제하고 DB에 반영
        members.delete(member);
        members.flush();
    }

    /**
     * METHOD: 전체 회원을 조회하고 응답 DTO 목록으로 변환
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> readAll() {
        return members.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
