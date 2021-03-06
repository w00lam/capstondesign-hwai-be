package com.hwai.backend.book.domain;

import com.hwai.backend.category.domain.Category;
import com.hwai.backend.user.domian.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private LocalDate due_date;

    private String current;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Book(String title, Category category, User user) {
        this.title = title;
        this.user = user;
        this.category = category;
    }

    public void lend(User user) {
        this.due_date = LocalDate.now().plusDays(14);
        this.current = "대출중";
        setUser(user);
    }

    public void returnBook(User user, String current) {
        this.current = current;
        user.getBooks().remove(this);
    }

    public void setUser(User user) {
        this.user = user;
        user.getBooks().add(this);
    }
}