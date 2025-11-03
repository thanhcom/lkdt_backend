package thanhcom.site.lkdt.utility;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecurityUtils {

    // Lấy username hiện tại
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof Jwt) {
            return ((Jwt) principal).getClaimAsString("sub"); // hoặc claim khác tùy JWT
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    // Lấy danh sách roles hiện tại
    public static List<String> getCurrentRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Collections.emptyList();
        }

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // Kiểm tra xem user có role cụ thể không
    public static boolean hasRole(String role) {
        return getCurrentRoles().contains(role);
    }

    // Lấy thông tin JWT claims (nếu sử dụng JWT)
    public static Map<String, Object> getJwtClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaims();
        }
        return Collections.emptyMap();
    }

    // Lấy username + roles + claims tiện lợi
    public static UserInfo getCurrentUserInfo() {
        String username = getCurrentUsername();
        List<String> roles = getCurrentRoles();
        Map<String, Object> claims = getJwtClaims();
        return new UserInfo(username, roles, claims);
    }

    // Class tiện lợi chứa thông tin user
    @Getter
    public static class UserInfo {
        private final String username;
        private final List<String> roles;
        private final Map<String, Object> claims;

        public UserInfo(String username, List<String> roles, Map<String, Object> claims) {
            this.username = username;
            this.roles = roles;
            this.claims = claims;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "username='" + username + '\'' +
                    ", roles=" + roles +
                    ", claims=" + claims +
                    '}';
        }
    }
}
