package thanhcom.site.lkdt.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.request.RoleRequest;
import thanhcom.site.lkdt.dto.request.UserRequest;
import thanhcom.site.lkdt.dto.response.UserResponse;
import thanhcom.site.lkdt.entity.Account;
import thanhcom.site.lkdt.mapper.AccountMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.AccountService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "User API", description = "API quản lý người dùng")
@RequestMapping("/account")
public class AccountController {
    AccountService accountService;
    AccountMapper accountMapper;
    @PostMapping("/create")
    public ResponseEntity<?> Create(@RequestBody UserRequest userRequest) {
        Account account = accountMapper.DtoToReq(userRequest);
        accountService.createAccount(account);
        ResponseApi<UserResponse> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ResToDto(account));
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Tạo tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> Update(@RequestBody UserRequest userRequest , @PathVariable Long id) {
        Account account = accountService.editAccount(id, userRequest);
        ResponseApi<UserResponse> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ResToDto(account));
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Cập nhật tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> GetById(@PathVariable Long id) {
        Account accountInfo = accountService.getAccountInfo(id);
        ResponseApi<UserResponse> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ResToDto(accountInfo));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy thông tin tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/{id}/set-roles")
    public ResponseEntity<?> SetRoles(@PathVariable Long id , @RequestBody Set<RoleRequest> roles) {
        Account account = accountService.setRolesToAccount(id, roles);
        ResponseApi<UserResponse> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ResToDto(account));
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Cập nhật vai trò tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/all")
    public ResponseEntity<?> GetAll() {
        List<Account> allAccounts = accountService.getAllAccounts();
        ResponseApi<List<UserResponse>> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ListResToDto(allAccounts));
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Lấy danh sách tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/my-info")
    public ResponseEntity<?> GetMyInfo() {
        Account accountInfo = accountService.getMyAccountInfo();
        ResponseApi<UserResponse> responseApi = new ResponseApi<>();
        responseApi.setData(accountMapper.ResToDto(accountInfo));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy thông tin tài khoản thành công");
        return ResponseEntity.ok(responseApi);
    }
}
