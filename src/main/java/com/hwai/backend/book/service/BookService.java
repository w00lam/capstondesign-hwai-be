package com.hwai.backend.book.service;

import com.hwai.backend.book.controller.dto.BookSaveRequestDto;
import com.hwai.backend.common.exception.BadRequestException;
import com.hwai.backend.common.exception.NotFoundException;
import com.hwai.backend.book.controller.dto.LendRequestDto;
import com.hwai.backend.book.domain.Book;
import com.hwai.backend.book.domain.BookRepository;
import com.hwai.backend.common.message.Message;
import com.hwai.backend.user.domian.User;
import com.hwai.backend.user.domian.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final String BOOK_SAVE_MESSAGE = "책 저장 성공";
    private static final String BOOK_NOT_FOUND_MESSAGE = "책이 존재하지 않습니다.";
    private static final String LEND_BOOK_MESSAGE = "책 대출 성공";
    private static final String USER_NOT_FOUND_MESSAGE = "해당 유저가 존재하지 않습니다.";

    @Transactional
    public Message save(BookSaveRequestDto bookSaveRequestDto) {
        Book book = bookSaveRequestDto.toEntity();
        bookRepository.save(book);
        return new Message(BOOK_SAVE_MESSAGE);
    }

    @Transactional
    public Message lend(LendRequestDto lendRequestDto) {
        for(Long id : lendRequestDto.getBook_id()){
            bookRepository.findById(id).orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND_MESSAGE));
        }
        User findUser = userRepository.findById(lendRequestDto.getUser_id())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        for(Long id : lendRequestDto.getBook_id()){
            Book findBook = findBookById(id);
            findBook.lend(findUser);
        }
        return new Message(LEND_BOOK_MESSAGE);
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BadRequestException(BOOK_NOT_FOUND_MESSAGE));
    }
}