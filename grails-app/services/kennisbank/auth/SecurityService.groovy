package kennisbank.auth

import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication

class SecurityService implements Serializable {

    static transactional = true

    def springSecurityService
    def authenticationManager

    void signIn(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password)
            //User details = new User(username);
            //authentication.setDetails(details);

            Authentication auth = authenticationManager.authenticate(authentication);

            SCH.getContext().setAuthentication(auth);
            // SCH.context.authentication = authenticationManager.authenticate(authentication)
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

    boolean isSignedIn() {
        Authentication auth = SCH.getContext().getAuthentication()

        if (auth != null && !auth.getName().equals("anonymousUser")) {
            return true
        }
        else { 
            return false
        }
    }
}