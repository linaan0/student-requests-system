package mk.ukim.finki.wp.molbi.config;

import mk.ukim.finki.wp.molbi.model.base.Professor;
import mk.ukim.finki.wp.molbi.model.base.Student;
import mk.ukim.finki.wp.molbi.model.base.User;
import mk.ukim.finki.wp.molbi.model.exceptions.InvalidUsernameException;
import mk.ukim.finki.wp.molbi.repository.UserRepository;
import mk.ukim.finki.wp.molbi.service.interfaces.ProfessorService;
import mk.ukim.finki.wp.molbi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class FacultyUserDetailsService implements UserDetailsService {

    @Value("${system.authentication.password}")
    String systemAuthenticationPassword;

    final UserRepository userRepository;

    final StudentRepository studentRepository;

    final ProfessorService professorService;

    final PasswordEncoder passwordEncoder;

    public FacultyUserDetailsService(UserRepository userRepository, StudentRepository studentRepository, ProfessorService professorService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.professorService = professorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.toLowerCase();
        User user = userRepository.findById(username).orElseThrow(InvalidUsernameException::new);
        if (user.getRole().isProfessor()) {
            Professor professor = professorService.getProfessorById(username);
            return new FacultyUserDetails(user, professor, passwordEncoder.encode(systemAuthenticationPassword));
        } else if (user.getRole().isStudent()) {
        Student student = studentRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));
        return new FacultyUserDetails(user, student,
                passwordEncoder.encode(systemAuthenticationPassword));
    }else {
            return new FacultyUserDetails(user, passwordEncoder.encode(systemAuthenticationPassword));
        }
    }
}
