package epasal.com.userservice.services;

import epasal.com.userservice.dtos.requests.CodeRequest;
import epasal.com.userservice.dtos.requests.VerifyCode;

public interface CodesService {
    void getCode(CodeRequest codeRequest);

    void verifyCode(VerifyCode verifyCode);
}
