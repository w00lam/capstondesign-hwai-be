package com.hwai.backend.user.service;

import com.hwai.backend.book.domain.Book;
import com.hwai.backend.common.exception.BadRequestException;
import com.hwai.backend.common.exception.NotFoundException;
import com.hwai.backend.common.message.Message;
import com.hwai.backend.user.controller.dto.*;
import com.hwai.backend.user.domian.User;
import com.hwai.backend.user.domian.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    private static final String SIGN_UP_SUCCESS_MESSAGE = "회원가입 성공";
    private static final String WITHDRAW_SUCCESS_MESSAGE = "회원탈퇴 성공";
    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 성공";
    private static final String LOGOUT_SUCCESS_MESSAGE = "로그아웃 성공";
    private static final String USER_NOT_FOUND_MESSAGE = "해당 유저가 존재하지 않습니다.";
    private static final String EMAIL_DUPLICATION_MESSAGE = "이메일 중복입니다.";
    private static final String PASSWORD_NOT_EQUAL_MESSAGE = "비밀번호가 일치하지 않습니다.";
    private static final String USER_INFO_MESSAGE = "회원 정보 불러오기 성공";
    private static final String UPDATE_PW_MESSAGE = "비밀번호 변경 완료";
    private static final String PASSWORD_EQUAL_MESSAGE = "이전 비밀번호와 동일합니다.";
    private static final String BOOK_LIST_IS_EMPTY = "대출중인 책이 없습니다.";
    private static final String LIST_IS_NOT_EMPTY = "대출중인 책이 있습니다.";
    private static final String SEND_MAIL_SUCCESS = "메일 전송 성공";

    @Transactional
    public Message join(JoinRequestDto joinRequestDto) {
        checkDuplicateEmail(joinRequestDto.getEmail());
        User user = joinRequestDto.toEntity();
        userRepository.save(user);
        return new Message(SIGN_UP_SUCCESS_MESSAGE);
    }

    @Transactional
    public Message withdraw(Long id) {
        User user = findUserById(id);
        checkList(user);
        userRepository.delete(user);
        return new Message(WITHDRAW_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User findUser = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        validatePasswordForEqual(loginRequestDto, findUser.getPw());
        return new LoginResponseDto(findUser.getId(), findUser.getName(), findUser.isAdmin(),
                123456, new Message(LOGIN_SUCCESS_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Message logout(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        return new Message(LOGOUT_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public UserPageResponseDto page(Long id) {
        User user = findUserById(id);
        return new UserPageResponseDto(user, new Message(USER_INFO_MESSAGE));
    }

    @Transactional
    public Message updatePw(PwUpdateRequestDto pwUpdateRequestDto) {
        User user = userRepository.findById(pwUpdateRequestDto.getId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        validatePasswordForNotEqual(pwUpdateRequestDto, user.getPw());
        user.updatePw(pwUpdateRequestDto);
        return new Message(UPDATE_PW_MESSAGE);
    }

    @Transactional(readOnly = true)
    public List<MyListResponseDto> viewMyList(Long id) {
        User user = findUserById(id);
        List<Book> books = user.getBooks();
        checkEmpty(books);
        return books.stream()
                .map(MyListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public MailDto createMailAndChangePwd(String userEmail) {
        String newPw = randomPw(7);
        MailDto mailDto = new MailDto();
        mailDto.setAddress(userEmail);
        mailDto.setTitle("임시비밀번호 안내 이메일입니다.");
        mailDto.setMessage(MessageFormat.format("안녕하세요.\n 임시비밀번호 안내 관련 이메일입니다.\n 회원님의 임시 비밀번호는 {0} 입니다.\n로그인 후에 꼭 비밀번호 변경을 해주세요.\n감사합니다.", newPw));
        userRepository.setpw(newPw, userEmail);

        return mailDto;
    }

    @Autowired
    public JavaMailSender javaMailSender;

    @Async
    public Message sendEmail(MailDto mailDto) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("wlam135@naver.com");
        simpleMessage.setTo(mailDto.getAddress());
        simpleMessage.setSubject(mailDto.getTitle());
        simpleMessage.setText(mailDto.getMessage());
        javaMailSender.send(simpleMessage);

        return new Message(SEND_MAIL_SUCCESS);
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException(EMAIL_DUPLICATION_MESSAGE);
        }
    }

    private void validatePasswordForEqual(LoginRequestDto loginRequestDto, String pw) {
        if (!(loginRequestDto.getPw()).equals(pw)) {
            throw new BadRequestException(PASSWORD_NOT_EQUAL_MESSAGE);
        }
    }

    private void validatePasswordForNotEqual(PwUpdateRequestDto pwUpdateRequestDto, String pw) {
        if ((pwUpdateRequestDto.getNew_pw()).equals(pw)) {
            throw new BadRequestException(PASSWORD_EQUAL_MESSAGE);
        }
    }

    private void checkList(User user) {
        if (!(user.getBooks().isEmpty())) {
            throw new BadRequestException(LIST_IS_NOT_EMPTY);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    private void checkEmpty(List<Book> bookList) {
        if (bookList.isEmpty()) {
            throw new BadRequestException(BOOK_LIST_IS_EMPTY);
        }
    }

    private String randomPw(int length) {
        int index;
        char[] charSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
                , 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'
                , 'l', 'n', 'm', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v'
                , 'w', 's', 'y', 'z'};

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            index = (int) (charSet.length * Math.random());
            stringBuilder.append(charSet[index]);
        }

        return stringBuilder.toString();
    }
}
