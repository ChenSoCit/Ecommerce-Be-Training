// package com.java.TrainningJV.controller;

// import com.java.TrainningJV.dtos.request.SignRequest;
// import com.java.TrainningJV.dtos.response.ApiResponse;
// import com.java.TrainningJV.services.AuthenticationService;
// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("api/v3/auth")
// @Slf4j(topic = "AUTHENTICATION-CONTROLLER")
// @Validated
// public class AuthenticationController {
//     // @Autowired
//     // private AuthenticationService authenticationService;

//     // @PostMapping("")
//     // public ApiResponse login(@Valid @RequestBody SignRequest signRequest) {
//     //     log.info("Login request: {}", signRequest);
//     //     return ApiResponse.builder()
//     //             .status(HttpStatus.OK.value())
//     //             .message("Login successful")
//     //             .data(authenticationService.login(signRequest))
//     //             .build();
//     // }
// }
