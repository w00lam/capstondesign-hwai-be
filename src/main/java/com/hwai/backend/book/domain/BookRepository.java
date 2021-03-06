package com.hwai.backend.book.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value =
            "SELECT * FROM book b INNER JOIN category c ON b.category_id = c.id " +
                    "WHERE b.current != c.shelf AND b.current != '대출중'"
            , nativeQuery = true)
    List<Book> findCheck();

    @Query(value =
            "SELECT * FROM book b WHERE b.user_id IS NULL"
            , nativeQuery = true)
    List<Book> findLendAble();

    @Query(value =
        "UPDATE book b SET b.due_date = NULL, b.user_id = NULL WHERE b.id = :bookId"
        , nativeQuery = true)
    void returnBook(@Param("bookId")Long bookId);
}
