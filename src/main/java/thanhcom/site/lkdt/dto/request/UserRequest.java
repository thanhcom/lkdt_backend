package thanhcom.site.lkdt.dto.request;

import lombok.*;
import thanhcom.site.lkdt.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String password;
    private String phone;
    private String email;
    private String fullname;
    private Boolean active;
    private LocalDateTime birthday;
    private Set<Role> roles;
}