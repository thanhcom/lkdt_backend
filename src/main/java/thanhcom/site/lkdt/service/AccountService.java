package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.dto.request.UserRequest;
import thanhcom.site.lkdt.entity.Account;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.AccountRepository;
import thanhcom.site.lkdt.utility.PassEncode;
import thanhcom.site.lkdt.utility.SecurityUtils;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    PassEncode passEncode;
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAllAccounts() {
        // lặp qua các claim trong JWT và in ra chúng từ tiện ích SecurityUtils
        Map<String, Object> jwtClaims = SecurityUtils.getJwtClaims();
        jwtClaims.forEach( (key, value) -> System.out.println("Claim: " + key + " = " + value));
        return accountRepository.findAll();
    }
    public Account getAccountInfo(Long id) {
        return accountRepository.findById(id).orElseThrow( ()->new AppException(ErrCode.USER_NOT_EXISTED));
    }

    public Account editAccount(Long id, UserRequest newAccount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));
        if (newAccount.getPassword() != null) {
            account.setPassword(passEncode.getPasswordEncoder().encode(newAccount.getPassword()));        }
        if (newAccount.getFullname() != null) {
            account.setFullname(newAccount.getFullname());
        }
        if (newAccount.getEmail() != null) {
            account.setEmail(newAccount.getEmail());
        }
        if (newAccount.getPhone() != null) {
            account.setPhone(newAccount.getPhone());
        }
        if (newAccount.getActive() != null) {
            account.setActive(newAccount.getActive());
        }
        if (newAccount.getBirthday() != null) {
            account.setBirthday(newAccount.getBirthday());
        }
        if (newAccount.getRoles() != null && !newAccount.getRoles().isEmpty()) {
            account.setRoles(newAccount.getRoles());
        }
        return accountRepository.save(account);
    }

    public void createAccount(Account account) {
        String password = passEncode.getPasswordEncoder().encode(account.getPassword());
        account.setPassword(password);
        accountRepository.save(account);
    }
}
