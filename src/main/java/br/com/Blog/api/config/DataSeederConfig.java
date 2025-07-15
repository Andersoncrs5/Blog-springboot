package br.com.Blog.api.config;

import br.com.Blog.api.entities.Role;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.RoleRepository;
import br.com.Blog.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataSeederConfig implements CommandLineRunner {

    @Value("${app.adm.email}")
    private String EMAIL_SUPER_ADM;
    @Value("${app.adm.name}")
    private String NAME_SUPER_ADM;
    @Value("${app.adm.password}")
    private String PASSWORD_SUPER_ADM;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Role adminSuperRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN")));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Optional<User> superadminOpt = userRepository.findByName("superadmin");

        if (superadminOpt.isEmpty()) {
            User superadmin = new User();
            superadmin.setName(NAME_SUPER_ADM);
            superadmin.setEmail(EMAIL_SUPER_ADM);
            superadmin.setPassword(passwordEncoder.encode(PASSWORD_SUPER_ADM));

            Set<Role> roles = new HashSet<>();
            roles.add(adminSuperRole);
            roles.add(adminRole);
            roles.add(userRole);

            superadmin.setRoles(roles);

            userRepository.save(superadmin);
            System.out.println("Superadmin created!");
        }

    }

}
