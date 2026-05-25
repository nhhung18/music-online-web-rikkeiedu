
package com.ra.base_spring_boot.util;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Lấy thông tin user hiện tại từ Security Context
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        }
        
        throw new RuntimeException("User not authenticated");
    }

    public static Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        return null;
    }

    /**
     * Lấy ID của user hiện tại
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Lấy username (email) của user hiện tại
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getEmail();
    }

    /**
     * Kiểm tra user có authenticated không
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               authentication.getPrincipal() instanceof MyUserDetails;
    }

    /**
     * Kiểm tra user có role cụ thể không
     */
    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
        }
        
        return false;
    }
}
