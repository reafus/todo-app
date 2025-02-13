package deeper.into.you.todo_app.notes.services;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Или выбросить исключение, если пользователь не аутентифицирован
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt) {
            return ((Jwt) principal).getClaim("sub");
        } else if (principal instanceof DefaultOidcUser) {
            return ((DefaultOidcUser) principal).getSubject(); // Используйте getSubject() для DefaultOidcUser
        } else {
            // Обработка других типов principal, если необходимо
            return null; // Или выбросить исключение, если тип principal не поддерживается
        }
    }

    public static boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

    public static void printTokenClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            jwtAuth.getToken().getClaims().forEach((k, v) ->
                    System.out.println(k + " : " + v)
            );
        }
    }
}
