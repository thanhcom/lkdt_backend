package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.Account;
import thanhcom.site.lkdt.entity.Role;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.AccountRepository;
import thanhcom.site.lkdt.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    AccountRepository accountRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getRolesByUser(Long userId) {
        Account account = accountRepository.findById(userId).orElseThrow(() -> new AppException(ErrCode.USER_NOT_EXISTED));
        return account.getRoles().stream().toList();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new AppException(ErrCode.ROLE_NOTFOUND));
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role CreateRole(Role role) {
        return roleRepository.save(role);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    

}
