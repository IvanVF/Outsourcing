package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.AuthResponse;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.services.*;
import com.antikryptonite.outsourcing.telegram.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Контроллер регистрации и аутентификации
 */
@RestController
public class AuthController {

    private final RegistrationService registrationService;

    private final AuthService authService;

    private TelegramSender telegramSender;

    /**
     * Конструктор контроллера
     */
    @Autowired
    public AuthController(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @Autowired
    public void setTelegramSender(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    /**
     * POST-запрос на регистрацию физического лица
     * @param registrationRequest - тело запроса с параметрами
     */
    @PostMapping("/register/individual")
    public void registerIndUser(@RequestBody @Valid RegistrationIndividualRequest registrationRequest) throws UniqueException {
        registrationService.saveIndProducer(registrationRequest);
    }

    /**
     * POST-запрос на регистрацию юридического лица
     * @param registrationRequest - тело запроса с параметрами
     */
    @PostMapping("/register/entity")
    public void registerEntUser(@RequestBody @Valid RegistrationEntityRequest registrationRequest) throws UniqueException {
        registrationService.saveEntProducer(registrationRequest);
    }

    /**
     * PUT-запрос на смену пароля
     * @param id - id пользователя
     * @param request - запрос с новым паролем
     */
    @PutMapping("/user/{id}/newpassword")                                             //todo: поправить юрл
    @PreAuthorize("#id.toString() == authentication.principal.username")
    @ResponseStatus(HttpStatus.NO_CONTENT)                                            //todo: неизвестно, что должно быть в ответе
    public void changePassword(@PathVariable("id") UUID id, @RequestBody @Valid ChangePasswordRequest request)
            throws ResourceNotFoundException, ValidationFallsException {
        registrationService.changePassword(id, request);
    }

    /**
     * GET-запрос на подтверждение аккаунта
     * @param code - личный номер пользователя
     */
    @GetMapping("/activate/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable UUID code) throws RegistrationFallsException {
        registrationService.activateProducer(code);
    }

    /**
     * POST-запрос на аутентификацию
     * @param authRequest - тело запроса с логином и паролем
     * @return - токены
     */
    @PostMapping("/auth")
    public AuthResponse auth(@Valid @RequestBody AuthRequest authRequest) throws ApplicationException {
        return authService.giveTokensByPassword(authRequest);
    }

    /**
     * Обновляет токены по рефреш-токену
     * @param refreshRequest рефреш-токен и фингерпринт
     * @return токены
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest refreshRequest) throws AuthWrongException {
        return authService.giveTokensByRefreshToken(refreshRequest);
    }

    /**
     * Тестовый метод
     */
    @GetMapping("/hello")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String testUser() {
        return "Hello, world!";
    }
}
