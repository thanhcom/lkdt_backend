package thanhcom.site.lkdt.dto.response;

import lombok.*;
import thanhcom.site.lkdt.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String username;
    private String phone;
    private String email;
    private String fullname;
    private Boolean active;
    private LocalDateTime datecreate;
    private LocalDateTime birthday;
    private LocalDateTime last_update;
    private Set<Role> roles;
}