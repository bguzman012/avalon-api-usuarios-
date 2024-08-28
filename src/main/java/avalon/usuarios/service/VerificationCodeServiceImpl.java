package avalon.usuarios.service;

import avalon.usuarios.data.MetodoPagoRepository;
import avalon.usuarios.data.VerificationCodeRepository;
import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.pojo.VerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository repository;

    @Autowired
    public VerificationCodeServiceImpl(VerificationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<VerificationCode> findByUsernameAndCodeAndUsedFalse(String username, String code) {
        return repository.findByUsernameAndCodeAndUsedFalse(username, code);
    }

    @Override
    public Optional<VerificationCode> findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(String username, String code, LocalDateTime currentTime) {
        return repository.findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(username, code, currentTime);
    }

    @Override
    public VerificationCode saveVerificationCode(VerificationCode verificationCode) {
        return repository.save(verificationCode);
    }

    @Override
    public void deleteVerificationCode(Long verificationCodeId) {
        repository.deleteById(verificationCodeId);
    }

}
