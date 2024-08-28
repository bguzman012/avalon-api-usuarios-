package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VerificationCode{

    public VerificationCode(String username, String code, LocalDateTime createdAt, LocalDateTime expiresAt, boolean used) {
        this.username = username;
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

}

