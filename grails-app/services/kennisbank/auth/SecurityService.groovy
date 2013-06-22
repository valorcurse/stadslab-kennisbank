package kennisbank.auth

import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder


class SecurityService {

    static transactional = true

    def springSecurityService
    def authenticationManager

    void signIn(String username, String password) {
        try {
            def authentication = new UsernamePasswordAuthenticationToken(username, password)
            SCH.context.authentication = authenticationManager.authenticate(authentication)
        } catch (BadCredentialsException e) {
            throw new SecurityException("&amp;amp;amp;amp;amp;quot;Invalid username/password&amp;amp;amp;amp;amp;quot;")
        }
    }

    void signOut(){
        SCH.context.authentication = null
    }

    String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); //get logged in username
    }

    boolean isSignedIn(){
        return springSecurityService.isLoggedIn()
    }
}