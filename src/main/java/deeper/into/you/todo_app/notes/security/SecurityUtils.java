package deeper.into.you.todo_app.notes.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

        if (principal instanceof Jwt) {
            logger.info("Jwt is authenticated");
            return ((Jwt) principal).getClaim("sub");
        } else if (principal instanceof DefaultOidcUser) {
            logger.info("OidcUser is authenticated");
            return ((DefaultOidcUser) principal).getSubject();
        } else {
            logger.info("unknown principal type");
            return null;
        }
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaim("preferred_username");
        } else if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getPreferredUsername();
        }
        return null;
    }

    public static boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }
}
