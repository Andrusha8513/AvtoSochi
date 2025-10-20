package ru.avtoAra.AvtoSochi.users.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.avtoAra.AvtoSochi.users.Role;
import ru.avtoAra.AvtoSochi.users.UserRepository;
import ru.avtoAra.AvtoSochi.users.Users;

import java.util.HashSet;
import java.util.Set;

@Component
public class AddAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AddAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        addAdmin();
    }

    public void addAdmin(){
        Users newAdmin = new Users();
        newAdmin.setEmail("1");
        newAdmin.setPassword(passwordEncoder.encode("11111111"));
        newAdmin.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_ADMIN);
        newAdmin.setRoles(roles);

        userRepository.save(newAdmin);
    }
}
