package org.sopt.member.controller;

import java.net.URI;
import org.sopt.common.response.ApiResponse;
import org.sopt.member.dto.CreateMemberRequest;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 회원 생성 삭제 HTTP 요청을 Service 위임하고 응답을 반환
 */
@RestController
@RequestMapping(value = "/api/v1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController implements MemberApi {
    private final MemberService service; // 예: Spring이 주입한 Service 객체
    /**
     * CONSTRUCTOR: 업무 처리에 필요한 저장소 또는 Service를 생성자로 주입
     */
    public MemberController(MemberService service) { this.service = service; }

    /**
     * METHOD: 회원 생성 요청을 Service에 전달하고 201 성공 응답을 반환
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(
            @RequestBody CreateMemberRequest req
    ){
        MemberResponse member = service.create(req.name());
        return ResponseEntity.created(URI.create("/api/v1/members/" + member.id()))
                .body(ApiResponse.success(MemberSuccessCode.MEMBER_CREATED, member));
    }

    /**
     * METHOD: 회원 삭제를 Service에 위임하고 성공 응답을 반환
     */
    @Override
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("memberId") Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MemberSuccessCode.MEMBER_DELETED, null));
    }
}

