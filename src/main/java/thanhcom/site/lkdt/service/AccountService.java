    package thanhcom.site.lkdt.service;

    import lombok.AccessLevel;
    import lombok.AllArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import thanhcom.site.lkdt.dto.request.RoleRequest;
    import thanhcom.site.lkdt.dto.request.UserRequest;
    import thanhcom.site.lkdt.entity.Account;
    import thanhcom.site.lkdt.entity.PasswordResetToken;
    import thanhcom.site.lkdt.entity.Role;
    import thanhcom.site.lkdt.enums.ErrCode;
    import thanhcom.site.lkdt.exception.AppException;
    import thanhcom.site.lkdt.repository.AccountRepository;
    import thanhcom.site.lkdt.repository.PasswordResetTokenRepository;
    import thanhcom.site.lkdt.repository.RoleRepository;
    import thanhcom.site.lkdt.utility.PassEncode;
    import thanhcom.site.lkdt.utility.SecurityUtils;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;
    import java.util.UUID;
    import java.util.stream.Collectors;

    @Service
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)

    public class AccountService {
        AccountRepository accountRepository;
        PassEncode passEncode;
        RoleRepository roleRepository;
        PasswordResetTokenRepository passwordResetTokenRepository;
        EmailService emailService;
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

        @Transactional
        public Account setRolesToAccount(Long id, Set<RoleRequest> roleRequests) {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));
            Set<Role> roles = roleRequests.stream()
                    .map(roleReq -> roleRepository.findByName(roleReq.getRole_name())
                            .orElseThrow(() -> new AppException(ErrCode.ROLE_NOTFOUND)))
                    .collect(Collectors.toSet());
            account.setRoles(roles);
            return accountRepository.save(account);
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

        // cách lọc chỉ trả về thông tin nếu trùng với thông tin mình đăng nhập
        //@PostAuthorize("returnObject.username == authentication.name")
        public Account getMyAccountInfo() {
            String currentUsername = SecurityUtils.getCurrentUsername();
            return accountRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));
        }

// Tạo token đặt lại mật khẩu và gửi email cho người dùng
        @Transactional
        public void createResetPasswordToken(String username) {
            Account user = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .id(token)
                    .username(username)
                    .expiryTime(LocalDateTime.now().plusMinutes(10))
                    .build();
            passwordResetTokenRepository.save(resetToken);
            //gửi email
            emailService.sendResetPasswordEmail(user.getEmail(), username, token);
        }
// Đặt lại mật khẩu sử dụng token
        @Transactional
        public void resetPassword(String token, String newPassword) {
            PasswordResetToken resetToken = passwordResetTokenRepository.findById(token)
                    .orElseThrow(() -> new AppException(ErrCode.TOKEN_INVALID));
            if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrCode.TOKEN_EXPIRED);
            }
            Account user = accountRepository.findByUsername(resetToken.getUsername())
                    .orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));
            user.setPassword(passEncode.getPasswordEncoder().encode(newPassword));
            accountRepository.save(user);
            passwordResetTokenRepository.delete(resetToken);
        }

    }
