package com.example.kosapp.services;

import com.example.kosapp.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class SecureService {

	private final OAuthUserService oauthUserService;

	public SecureService(OAuthUserService oauthUserService) {
		this.oauthUserService = oauthUserService;
	}

	public void bindSessionUser(HttpSession session, OAuth2User oauthUser) {
		User user = oauthUserService.upsertFromOAuth(oauthUser);
		session.setAttribute("user", user);
		session.setAttribute("role", oauthUserService.resolveRoleName(user));
	}
}
