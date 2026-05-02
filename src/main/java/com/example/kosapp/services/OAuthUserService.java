package com.example.kosapp.services;

import com.example.kosapp.models.Role;
import com.example.kosapp.models.User;
import com.example.kosapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OAuthUserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final Set<String> adminEmails;

    public OAuthUserService(
            UserRepository userRepository,
            RoleService roleService,
            @Value("${app.admin-emails:}") String adminEmails
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public User upsertFromOAuth(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google account missing email");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User created = new User();
            created.setUsername(email);
            created.setPassword("oauth2");
                Role defaultRole = isAdminEmail(email)
                    ? roleService.getOrCreate("ADMIN")
                    : roleService.getOrCreate("STAFF");
            created.getRoles().add(defaultRole);
            return created;
        });

        user.setEmail(email);
        user.setFullName(oauthUser.getAttribute("name"));
        user.setPictureUrl(oauthUser.getAttribute("picture"));
        user.setProvider("GOOGLE");
        user.setProviderId(oauthUser.getAttribute("sub"));

        if (isAdminEmail(email)) {
            Role adminRole = roleService.getOrCreate("ADMIN");
            user.getRoles().add(adminRole);
        }

        return userRepository.save(user);
    }

    public User findByEmailOrNull(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    public String resolveRoleName(User user) {
        if (user == null || user.getRoles() == null) {
            return "STAFF";
        }
        if (hasRole(user, "ADMIN")) return "ADMIN";
        if (hasRole(user, "STAFF")) return "STAFF";
        return "STAFF";
    }

    private boolean hasRole(User user, String roleName) {
        for (Role role : user.getRoles()) {
            if (roleName.equalsIgnoreCase(role.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminEmail(String email) {
        return email != null && adminEmails.contains(email.toLowerCase());
    }
}
