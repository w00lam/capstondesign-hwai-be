package com.hwai.backend.category.controller.dto;

import com.hwai.backend.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShelfResponseDto {
    private String genre;
    private String row;

    @Builder
    public ShelfResponseDto(Category category){
        this.genre = category.getGenre();
        this.row = category.getShelf();
    }
}
