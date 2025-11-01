package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByUsernameAndPassword(String username, String password);

    Optional<Account> findByUsername(String username);
}