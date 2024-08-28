package avalon.usuarios.service;

import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.pojo.VerificationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeService {

    Optional<VerificationCode> findByUsernameAndCodeAndUsedFalse(String username, String code);
    Optional<VerificationCode> findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(String username, String code, LocalDateTime currentTime);
    VerificationCode saveVerificationCode(VerificationCode verificationCode);
    void deleteVerificationCode(Long verificationCodeId);

}
