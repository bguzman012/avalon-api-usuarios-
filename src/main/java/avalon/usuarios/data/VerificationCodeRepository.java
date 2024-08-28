package avalon.usuarios.data;

import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.pojo.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByUsernameAndCodeAndUsedFalse(String username, String code);
    Optional<VerificationCode> findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(String username, String code, LocalDateTime currentTime);

}
