package com.hwai.backend.book.controller.dto;

import com.hwai.backend.book.domain.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChecklistResponseDto {
    private String title;
    private LocalDateTime due_date;
    private String shelf;
    private String current;

    @Builder
    public ChecklistResponseDto(Book book) {
        this.title = book.getTitle();
        this.due_date = book.getDue_date();
        this.shelf = book.getShelf();
        this.current = book.getCurrent();
    }
}