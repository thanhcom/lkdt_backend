package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thanhcom.site.lkdt.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,String> {
}
