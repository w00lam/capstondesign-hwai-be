package com.hwai.backend.controller.book;

import com.hwai.backend.controller.book.dto.LendRequestDto;
import com.hwai.backend.domain.book.BookRepository;
import com.hwai.backend.domain.message.Message;
import com.hwai.backend.service.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@RestController
public class BookController {

    private final BookService bookService;

    @PutMapping("/lend")
    @ResponseStatus(HttpStatus.OK)
    public void lend(@RequestBody LendRequestDto lendRequestDto) {
        Message message = bookService.lend(lendRequestDto);
        log.info(message.getMessage());
    }
}
