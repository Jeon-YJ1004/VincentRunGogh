package com.vincentrungogh.domain.board.controller;

import com.vincentrungogh.domain.board.service.BoardService;
import com.vincentrungogh.domain.board.service.dto.request.SaveBoardRequestDto;
import com.vincentrungogh.domain.board.service.facade.BoardFacade;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.util.CommonSwaggerResponse;
import com.vincentrungogh.global.util.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardFacade boardFacade;
    private final BoardService boardService;

    // 게시글 전체 조회

    @Operation(summary = "게시글 전체 조회", description = "커뮤니티 게시글 타입별로 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회에 성공했습니다.",
                content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "게시글 내용을 불러오기 실패했습니다.",
                content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping
    public ResponseEntity<?> getBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String type,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        log.info("getBoard 메소드가 호출되었습니다.");
        log.info("로그 인포 게시글 타입: " +type+" "+lat+" "+lng);

        FindBoardResponseDto responseDto = boardFacade.getBoard(userPrincipal, type, lat, lng);

        log.info("responseDto: " +responseDto);
        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "게시글이 전체 조회되었습니다.", responseDto));
    }

    // 게시글 생성

    @Operation(summary = "게시글 생성", description = "커뮤니티에 내가 쓴 게시글 넣기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "커뮤니티에 게시글 생성에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping
    public ResponseEntity<?> createBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody SaveBoardRequestDto requestDto) {
        log.info("createBoard 메소드가 호출되었습니다.");
        log.info("게시글 생성: " + requestDto.toString());

        boardFacade.createBoard(userPrincipal, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "게시글 생성 완료되었습니다.",null));
    }

    // 게시글 좋아요
    @Operation(summary = "게시글 좋아요 실행", description = "커뮤니티 게시글 좋아요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "좋아요 처리에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/likes/{boardId}")
    public ResponseEntity<?> addLikeBoard(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @PathVariable int boardId) {
        log.info("addLikesBoard 메소드가 호출되었습니다. boardId: " + boardId);

        boardService.addLike(userPrincipal, boardId);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "좋아요에 성공했습니다.", null));
    }

    // 게시글 좋아요 취소
    @Operation(summary = "게시글 좋아요 취소", description = "커뮤니티 게시글 좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 취소에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "좋아요 취소 처리에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @DeleteMapping("/likes/{boardId}")
    public ResponseEntity<?> deleteLikeBoard(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @PathVariable int boardId) {
        log.info("deleteLikeBoard 메소드가 호출되었습니다. boardId: " + boardId);

        boardService.deleteLike(userPrincipal, boardId);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "좋아요 취소에 성공했습니다.", null));
    }

}
