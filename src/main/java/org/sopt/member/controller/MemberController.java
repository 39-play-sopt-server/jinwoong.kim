package org.sopt.member.controller;

import org.sopt.common.response.Response;
import org.sopt.member.dto.CreateMemberRequest;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 회원 생성 삭제 HTTP 요청을 Service 위임하고 응답을 반환
 */
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService service; // 예: Spring이 주입한 Service 객체
    /**
     * CONSTRUCTOR: 업무 처리에 필요한 저장소 또는 Service를 생성자로 주입
     */
    public MemberController(MemberService service) { this.service = service; }

    /**
     * METHOD: 회원 생성 요청을 Service에 전달하고 201 성공 응답을 반환
     */
    @PostMapping
    public ResponseEntity<Response<MemberResponse>> create(
            @RequestBody CreateMemberRequest req
    ){
        return ResponseEntity.status(201).body(
                Response.success("회원이 생성되었습니다", service.create(req.name())));
    }

    /**
     * METHOD: 회원 삭제를 Service에 위임하고 성공 응답을 반환
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Response<Void>> delete(
            @PathVariable("memberId") Long id) {
        service.delete(id);
        return ResponseEntity.ok(Response.success("회원이 삭제되었습니다.", null));
    }
}

