package com.jangdu.community.fixture;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class UserFixture {

    public static final String EMAIL = "test@test.com";
    public static final String PASSWORD = "Test1234!";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String NICKNAME = "테스트";

    public static User createUser() {
        User user = User.create(EMAIL, ENCODED_PASSWORD, NICKNAME);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static User createUser(Long id, String email) {
        User user = User.create(email, ENCODED_PASSWORD, NICKNAME);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static SignupRequest createSignupRequest() {
        return new SignupRequest(EMAIL, PASSWORD, NICKNAME);
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }
}
