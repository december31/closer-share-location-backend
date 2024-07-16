package com.harian.share.location.closersharelocation.auth;

import com.harian.share.location.closersharelocation.auth.dto.AuthenticationRequest;
import com.harian.share.location.closersharelocation.auth.dto.AuthenticationResponse;
import com.harian.share.location.closersharelocation.auth.dto.OtpAuthenticationRequest;
import com.harian.share.location.closersharelocation.auth.dto.RegisterRequest;
import com.harian.share.location.closersharelocation.auth.dto.RequestOtpRequest;
import com.harian.share.location.closersharelocation.config.JwtService;
import com.harian.share.location.closersharelocation.exception.EmailAlreadyExistedException;
import com.harian.share.location.closersharelocation.exception.OtpAuthenticationException;
import com.harian.share.location.closersharelocation.exception.TokenAuthenticationException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.post.Post;
import com.harian.share.location.closersharelocation.post.PostRepository;
import com.harian.share.location.closersharelocation.token.Token;
import com.harian.share.location.closersharelocation.token.TokenRepository;
import com.harian.share.location.closersharelocation.token.TokenType;
import com.harian.share.location.closersharelocation.user.model.Gender;
import com.harian.share.location.closersharelocation.user.model.Role;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.utils.Constants;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    public AuthenticationResponse register(RegisterRequest request)
            throws EmailAlreadyExistedException, OtpAuthenticationException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new OtpAuthenticationException("otp authentication failed, please request otp first"));

        if (user.getPassword() != null) {
            throw new EmailAlreadyExistedException(
                    "An account with email '" + request.getEmail() + "' already existed");
        }

        if (request.getOtp() == null) {
            throw new OtpAuthenticationException("otp is missing, please provide otp to register");
        } else if (user == null || user.getOtp() == null) {
            throw new OtpAuthenticationException("otp authentication failed, please request otp first");
        } else {
            if (System.currentTimeMillis() - user.getOtpRequestedTime() > Constants.OTP_VALID_TIME) {

                if (passwordEncoder.matches(request.getOtp(), user.getOtp())) {
                    request.fillBlankValue();

                    user.setOtp(null);
                    user.setOtpRequestedTime(null);
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    user.setGender(request.getGender());
                    user.setDescription(request.getDescription());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setRole(request.getRole());
                    user.setAvatar(request.getAvatar());
                    user.setCreatedTime(System.currentTimeMillis());
                    user.setLastModified(System.currentTimeMillis());

                    user = userRepository.save(user);
                    var jwtToken = jwtService.generateToken(user);
                    var refreshToken = jwtService.generateRefreshToken(user);
                    saveUserToken(user, jwtToken);
                    return AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .build();
                } else {
                    throw new OtpAuthenticationException("incorrect otp");
                }
            } else {
                throw new OtpAuthenticationException("otp is expired");
            }
        }
    }

    public void requestOtp(RequestOtpRequest request) throws UnsupportedEncodingException, MessagingException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(User.builder()
                        .email(request.getEmail())
                        .name(request.getName())
                        .build());
        Integer otp = mailService.sendOTP(user);
        System.out.println(request.getEmail() + " -- otp: ******");
        String encodedOtp = passwordEncoder.encode(otp.toString());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName("User" + otp);
        }
        user.setOtp(encodedOtp);
        user.setOtpRequestedTime(System.currentTimeMillis());
        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found for email '" + request.getEmail() + "'"));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(OtpAuthenticationRequest request) throws UserNotFoundException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new OtpAuthenticationException("otp authentication failed, please request otp first"));

        if (request.getOtp() == null) {
            throw new OtpAuthenticationException("otp is missing, please provide otp to register");
        } else if (user.getOtp() == null) {
            throw new OtpAuthenticationException("otp authentication failed, please request otp first");
        } else {
            if (System.currentTimeMillis() - user.getOtpRequestedTime() > Constants.OTP_VALID_TIME) {

                if (passwordEncoder.matches(request.getOtp(), user.getOtp())) {
                    user.setOtp(null);
                    user.setOtpRequestedTime(null);
                    userRepository.save(user);

                    var jwtToken = jwtService.generateToken(user);
                    var refreshToken = jwtService.generateRefreshToken(user);
                    revokeAllUserTokens(user);
                    saveUserToken(user, jwtToken);
                    return AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .build();
                } else {
                    throw new OtpAuthenticationException("incorrect otp");
                }
            } else {
                throw new OtpAuthenticationException("otp is expired");
            }
        }

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(String token) throws IOException, TokenAuthenticationException {

        final String refreshToken;
        final String userEmail;
        if (token == null || !token.startsWith("Bearer ")) {
            throw new TokenAuthenticationException("Refresh token not found");
        }
        refreshToken = token.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                return authResponse;
            }
        }
        throw new TokenAuthenticationException("Authentication failed");
    }

    public void initSampleData() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(User.builder()
                    .name("User" + (i + 1))
                    .email("user" + (i + 1) + "@gmail.com")
                    .gender(Math.random() > 0.5 ? Gender.FEMALE : Gender.MALE)
                    .description("user" + (i + 1) + "desctiontion")
                    .password("$2a$10$maRFadNykQea2pNVdaqZhuZLjLy3/U4FM4MxwPtcq/KVnxAJpfIF2")
                    .role(Role.USER)
                    .avatar(("avatar/avatar" + (int) Math.ceil(Math.random() * 12) + ".png"))
                    .createdTime(System.currentTimeMillis())
                    .lastModified(System.currentTimeMillis())
                    .build());
        }
        List<User> savedUsers = userRepository.saveAll(users);
        savedUsers.forEach(user -> {
            Post post = Post.builder()
                    .title("Hello world!")
                    .content("post content")
                    .owner(user)
                    .createdTime(System.currentTimeMillis())
                    .lastModified(System.currentTimeMillis())
                    .build();
            postRepository.save(post);

            // FriendRequest friendRequest = new FriendRequest(friend, user, System.currentTimeMillis());
            // if (friend.getFriendRequestOf() == null) {
            //     friend.setFriendRequests(new HashSet<>());
            // }
            // friend.getFriendRequests().add(friendRequest);
            // userRepository.save(friend);
        });

    }
}
