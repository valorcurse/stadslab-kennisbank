package kennisbank.auth

import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class SecurityService implements Serializable {

    static transactional = true

    def springSecurityService
    def authenticationManager

    void signIn(String username, String password) {
        try {
            def authentication = new UsernamePasswordAuthenticationToken(username, password)
            SCH.context.authentication = authenticationManager.authenticate(authentication)
        } catch (BadCredentialsException e) {
            throw new SecurityException("Invalid username/password")
        }
    }

    void signOut(){
        SCH.context.authentication = null
    }

    String getCurrentUsername() {
        Authentication auth = SCH.getContext().getAuthentication()
        return auth.getName(); //get logged in username
    }

    boolean isSignedIn(){
        return springSecurityService.isLoggedIn()
    }
}