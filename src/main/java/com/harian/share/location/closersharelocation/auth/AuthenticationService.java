package com.harian.share.location.closersharelocation.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harian.share.location.closersharelocation.auth.dto.AuthenticationRequest;
import com.harian.share.location.closersharelocation.auth.dto.AuthenticationResponse;
import com.harian.share.location.closersharelocation.auth.dto.RegisterRequest;
import com.harian.share.location.closersharelocation.auth.dto.RequestOtpRequest;
import com.harian.share.location.closersharelocation.config.JwtService;
import com.harian.share.location.closersharelocation.exception.EmailAlreadyExistedException;
import com.harian.share.location.closersharelocation.exception.OtpAuthenticationException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.token.Token;
import com.harian.share.location.closersharelocation.token.TokenRepository;
import com.harian.share.location.closersharelocation.token.TokenType;
import com.harian.share.location.closersharelocation.user.User;
import com.harian.share.location.closersharelocation.user.UserRepository;
import com.harian.share.location.closersharelocation.utils.Constants;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
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

                System.out.println(request.getOtp());
                System.out.println(request.getOtp());
                System.out.println(passwordEncoder.encode(user.getOtp()));

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

                    user = userRepository.save(user);
                    var jwtToken = jwtService.generateToken(user);
                    var refreshToken = jwtService.generateRefreshToken(user);
                    saveUserToken(user, jwtToken);
                    return AuthenticationResponse.builder()
                            .user(user)
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
                .orElse(User.builder().email(request.getEmail()).build());
        Integer otp = mailService.sendOTP(user);
        String encodedOtp = passwordEncoder.encode(otp.toString());
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
                .user(user)
                .build();
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

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
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
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
