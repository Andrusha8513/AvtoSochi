package ru.avtoAra.AvtoSochi.users;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private final EmailService emailService;


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SessionRegistry sessionRegistry,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
        this.emailService = emailService;
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users createUsers(Users users) {
        if (userRepository.findByEmail(users.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Почта  занят");
        }


        if (users.getPassword() == null || users.getPassword().isEmpty() || users.getPassword().length() < 8) {
            throw new IllegalArgumentException("Пароль не должен быть короче 8 символов и не должен быть пустым");
        }


        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setRoles(Set.of(Role.ROLE_USER));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setConfirmationCode(code);
        users.setEnabled(false);

        Users saveUser = userRepository.save(users);
        emailService.sendConfirmationEmail(users.getEmail(), code);

        return saveUser;
    }
    @Transactional
    public boolean confirmRegistration(String code) {
        Optional<Users> usersOptional = userRepository.findByConfirmationCode(code);

        if (usersOptional.isPresent()) {
            Users user = usersOptional.get();
            user.setEnabled(true);
            user.setConfirmationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void resendConfirmationCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.isEnabled()) {
            throw new IllegalArgumentException("Аккаунт уже активирован");
        }

        String newConfirmationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setConfirmationCode(newConfirmationCode);
        userRepository.save(users);

        emailService.sendConfirmationEmail(users.getEmail(), newConfirmationCode);

    }


    @Transactional
    public void sendPasswordResetCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("пользователь с такой почтой не найден"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime expireDate = LocalDateTime.now().plusMinutes(15);

        users.setPasswordResetCode(code);
        users.setPasswordResetCodeExpiryDate(expireDate);
        userRepository.save(users);

        emailService.sendPasswordResetCodeEmail(users.getEmail(), code);
    }

    @Transactional
    public void resetPasswordWithCode(String email, String code, String newPassword) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getPasswordResetCode() == null || !users.getPasswordResetCode().equals(code)) {
            throw new IllegalArgumentException("Неверный код для сброса пароля");
        }

        if (LocalDateTime.now().isAfter(users.getPasswordResetCodeExpiryDate())) {
            throw new IllegalArgumentException("Срок действия  кода истёк");
        }

        String encodePassword = passwordEncoder.encode(newPassword);
        users.setPassword(encodePassword);
        users.setPasswordResetCode(null);
        users.setPasswordResetCodeExpiryDate(null);
        userRepository.save(users);
    }


    @Transactional
    public void requestEmailChange(Users users, String newEmail) {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Почта уже занята");
        }


        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setEmailChangeCode(code);
        users.setPendingEmail(newEmail);

        userRepository.save(users);
        emailService.sendConfirmationEmail(newEmail, code);

    }

    @Transactional
    public void resendEmailChangeCode(String pendingEmail){
        Users users = userRepository.findByPendingEmail(pendingEmail)
                .orElseThrow( () -> new IllegalArgumentException("Почта не найдена"));

        String code = UUID.randomUUID().toString().substring(0 , 6).toUpperCase();

        users.setEmailChangeCode(code);
        userRepository.save(users);
        emailService.sendConfirmationEmail(pendingEmail , code);
    }

    @Transactional
    public void confirmEmailChange(Users users, String code) {
      if(userRepository.findById(users.getId()).isEmpty()){
          throw new IllegalArgumentException("Пользователь не найден");
      }

        if (users.getEmailChangeCode() == null || !users.getEmailChangeCode().equals(code)) {
            throw new IllegalArgumentException("Неверный код подтверждения.");
        }

        users.setEmail(users.getPendingEmail());

        users.setPendingEmail(null);
        users.setEmailChangeCode(null);
        userRepository.save(users);
    }

    @Transactional
    public void updateUserPassword(Users users ,
                                   String newPassword,
                                   String currenPassword) {
      if(userRepository.findById(users.getId()).isEmpty()){
          throw new IllegalArgumentException("Пользователь не найден");
      }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (currenPassword == null || currenPassword.isEmpty()) {
                throw new IllegalArgumentException("Должен предоставлен быть текущий пароль");
            }
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Пароль не должен быть короче 8 символов");
            }
            if (!passwordEncoder.matches(currenPassword , users.getPassword())){
                throw new IllegalArgumentException("Текущий пароль не верен");
            }
            users.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(users);
    }

    public Users findEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя нет"));
    }

    @Transactional
    public void updateRoles(Long id, Set<Role> newRole) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Такой пользователь не найден"));

        if (newRole == null && newRole.isEmpty()) {
            throw new IllegalArgumentException("Роли могут пыть пустыми");
        }
        users.setRoles(newRole);
        userRepository.save(users);

        expireUserSessions(users.getEmail());
    }

    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя  с таким id не найдено"));

    }

    // разобраться над методом
    public void expireUserSessions(String username) {
        System.out.println("Attempting to expire sessions for: " + username);

        // Прохожу  по всем принципалам в реестре сессий
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            String principalName;

            // Оопределяю  тип принципала и получаю     имя пользователя
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                principalName = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else if (principal instanceof String) {
                principalName = (String) principal;
            } else if (principal instanceof Users) {
                principalName = ((Users) principal).getEmail();
            } else {

                continue;
            }

            System.out.println("Found principal: " + principalName);

            // если нашёл нужного пользователя
            if (principalName.equals(username)) {
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                System.out.println("Found " + sessions.size() + " sessions for user: " + username);

                // Завершаем все сессии
                sessions.forEach(session -> {
                    System.out.println("Expiring session: " + session.getSessionId());
                    session.expireNow();
                });

                return; //выход после обработки
            }
        }

        System.out.println("No sessions found for user: " + username);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteInactiveUsers() {;
        LocalDateTime twentyFourHoursAho = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        List<Users> inactiveUsers = userRepository.findByEnabledIsFalseAndRegistrationDateBefore(twentyFourHoursAho);

        if (!inactiveUsers.isEmpty()) {
            userRepository.deleteAll(inactiveUsers);
            System.out.println("Удалено " + inactiveUsers.size() + "неактивных пользователей");
        }
    }

    public void deleteUsers(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Пользователя с таким id=%s нет".formatted(id));
        }
    }


}

//    @Transactional
//    public Users putUsers(Long id,
//                         String newEmail,
//                         String password,
//                         String currentPassword) {
//        var user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким id=%s не найден.".formatted(id)));
//
//
//        if (password != null && password.isEmpty()) {
//            if (currentPassword == null || currentPassword.isEmpty()) {
//                throw new IllegalArgumentException("Текущий пароль должен быть предоставлен для смены пароля");
//            }
//            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
//                throw new IllegalArgumentException("Пароли не совпадают");
//            }
//        }
//
//        if (password != null && !password.isEmpty()) {
//            if (password.length() < 8) {
//                throw new IllegalArgumentException("Пароль должен быть не короче 8 символов");
//            }
//            user.setPassword(passwordEncoder.encode(password));
//        }
//

/// /        if (email != null && !email.isEmpty() && !user.getEmail().equals(email)) {
/// /            Optional<Users> optionalUsers = userRepository.findByEmail(email);
/// /            if (optionalUsers.isPresent()) {
/// /                throw new IllegalArgumentException("Почта уже занят");
/// /            }
/// /            user.setEmail(email);
/// /        }
//
//        if(newEmail != null && !newEmail.isEmpty() && !user.getEmail().equals(newEmail)){
//            if(userRepository.findByEmail(newEmail).isPresent()){
//                throw new IllegalArgumentException("Это почта уже занята.");
//            }
//
//            String code = UUID.randomUUID().toString().substring(0 ,6).toUpperCase();
//            user.setConfirmationCode(code);
//            user.setEnabled(false);
//            emailService.sendConfirmationEmail(newEmail , code);
//        }
//
//        return userRepository.save(user);
//
//
//    }