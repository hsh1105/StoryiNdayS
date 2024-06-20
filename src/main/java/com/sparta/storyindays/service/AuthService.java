package com.sparta.storyindays.service;

import com.sparta.storyindays.config.JwtConfig;
import com.sparta.storyindays.dto.user.Auth;
import com.sparta.storyindays.dto.user.LoginReqDto;
import com.sparta.storyindays.dto.user.SignupReqDto;
import com.sparta.storyindays.entity.PasswordHistory;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.jwt.JwtProvider;
import com.sparta.storyindays.repository.PasswordHistoryRepository;
import com.sparta.storyindays.repository.UserRepository;
import com.sparta.storyindays.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthService")
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public void signup(SignupReqDto signupReqDto) {

        String username = signupReqDto.getUsername();
        String password = passwordEncoder.encode(signupReqDto.getPassword());
        String name = signupReqDto.getName();
        Auth auth = signupReqDto.getAuthType();
        String email = signupReqDto.getEmail();

        if (userRepository.findByUsername(signupReqDto.getName()).isPresent()) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "already.exist.username",
                    null,
                    "Already exist username",
                    Locale.getDefault()
            ));
        }

        User user = new User(username, password, name, auth, email);

        userRepository.save(user);

        //회원가입시 비밀번호가 히스토리에 저장
        PasswordHistory newHistory = new PasswordHistory();
        newHistory.setUser(user);
        newHistory.setPassword(user.getPassword());
        passwordHistoryRepository.save(newHistory);
    }

    @Transactional
    public String login(LoginReqDto loginReqDto) {
        log.info("로그인 시도");
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReqDto.getUsername(),
                        loginReqDto.getPassword(),
                        null
                )
        );

        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

        String accessToken = jwtProvider.createToken(user, JwtConfig.accessTokenTime);
        String refreshToken = jwtProvider.createToken(user, JwtConfig.refreshTokenTime);

        user.updateRefreshToken(refreshToken);
        log.info("로그인 완료");
        return accessToken;
    }


}
