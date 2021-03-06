package com.hwai.backend.book.controller;

import com.hwai.backend.book.controller.dto.*;
import com.hwai.backend.common.message.Message;
import com.hwai.backend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@RestController
public class BookController {
    private static final String VIEW_CHECK_LIST_SUCCESS_MESSAGE = "체크리스트 조회 성공";
    private static final String VIEW_ABLE_LIST_SUCCESS_MESSAGE = "대출가능 도서 조회 성공";

    private final BookService bookService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Message save(@RequestBody BookSaveRequestDto bookSaveRequestDto) {
        Message message = bookService.save(bookSaveRequestDto);
        log.info(message.getMessage());
        return message;
    }

    @PatchMapping("/lend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void lend(@RequestBody LendRequestDto lendRequestDto) {
        Message message = bookService.lend(lendRequestDto);
        log.info(message.getMessage());
    }

    @GetMapping("/checklist")
    @ResponseStatus(HttpStatus.OK)
    public List<ChecklistResponseDto> viewCheckList() {
        List<ChecklistResponseDto> checklistResponseDtoList = bookService.findCheck();
        log.info(VIEW_CHECK_LIST_SUCCESS_MESSAGE);
        return checklistResponseDtoList;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<LendAbleListResponseDto> viewLendAbleList() {
        List<LendAbleListResponseDto> lendAbleListResponseDtoList = bookService.findLendAble();
        log.info(VIEW_ABLE_LIST_SUCCESS_MESSAGE);
        return lendAbleListResponseDtoList;
    }

    @PatchMapping("/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void re_turn(@RequestBody ReturnBookRequestDto returnBookRequestDto) {
        Message message = bookService.returnBook(returnBookRequestDto);
        log.info(message.getMessage());
    }
}
