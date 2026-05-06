package epasal.com.userservice.controller;

import epasal.com.userservice.dtos.requests.CodeRequest;
import epasal.com.userservice.dtos.requests.VerifyCode;
import epasal.com.userservice.dtos.response.ApiResponse;
import epasal.com.userservice.services.CodesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static epasal.com.userservice.constant.ApiConstant.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
class OTPController {
    private final CodesService codesService;

    @PostMapping(VERIFICATION_CODE)
    public ApiResponse<Void> generateVerificationCode(@Valid @RequestBody CodeRequest codeRequest) {
        codesService.getCode(codeRequest);
        return ApiResponse.success("If the account exists, OTP has been sent.", null);
    }

    @PostMapping(VERIFY)
    public ApiResponse<Void> verifyCode(@Valid @RequestBody VerifyCode verifyCode) {
        codesService.verifyCode(verifyCode);
        return ApiResponse.success("Code verified successfully", null);
    }

}
