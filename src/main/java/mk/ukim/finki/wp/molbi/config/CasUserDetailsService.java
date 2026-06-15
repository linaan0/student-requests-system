package mk.ukim.finki.wp.molbi.config;

import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import mk.ukim.finki.wp.molbi.repository.UserRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.ProfessorService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Profile("cas")
@Service
public class CasUserDetailsService extends FacultyUserDetailsService implements AuthenticationUserDetailsService {


    public CasUserDetailsService(UserRepository userRepository,
                                 StudentRepository studentRepository,
                                 ProfessorService professorService,
                                 PasswordEncoder passwordEncoder) {
        super(userRepository, studentRepository,professorService,  passwordEncoder);
    }

    @Override
    public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
        String username = (String) token.getPrincipal();
        return super.loadUserByUsername(username);
    }
}
