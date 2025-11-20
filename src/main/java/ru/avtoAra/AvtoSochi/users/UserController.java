package ru.avtoAra.AvtoSochi.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }




    @GetMapping
    public List<Users> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Users user = userService.findEmail(userDetails.getUsername());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        try {
            Users createdUser = userService.createUsers(user);
            return new ResponseEntity<>("Registration successful. Please check your email to confirm.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("code") String code) {
        boolean isConfirmed = userService.confirmRegistration(code);
        if (isConfirmed) {
            return ResponseEntity.ok("Аккаунт успешно подтвержден! Теперь вы можете войти.");
        } else
            return ResponseEntity.badRequest().body("Неверный или устаревший код подтверждения.");
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendConfirmationCode(@RequestParam String email) {
        try {
            userService.resendConfirmationCode(email);
            return ResponseEntity.ok("Новый код подтверждения был отправлен на ваш email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/resend-pendingEmail-code")
    public ResponseEntity<?> resendPendingCode(@RequestParam String pendingEmail){
        try {
            userService.resendEmailChangeCode(pendingEmail);
            return ResponseEntity.ok("Новый код отправлен");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/reset-confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String newPassword = payload.get("newPassword");
        try {
            userService.resetPasswordWithCode(email, code, newPassword);
            return ResponseEntity.ok("Пароль успешно обновлён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
            userService.sendPasswordResetCode(email);
            return ResponseEntity.ok("Если аккаунт с таким email существует, код для сброса пароля отправлен.");
        } catch (IllegalArgumentException e) {
            // для безопасности всегда возвращаем одинаковый ответ
            //надо фиксануть и добавить на проверку на существование почты в бд
            return ResponseEntity.ok("Если аккаунт с таким email существует, код для сброса пароля отправлен.");
        }
    }


    @PostMapping("/request-email-change")
    public ResponseEntity<?> requestEmailChange(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody Map<String, String> payload) {
        try {
            String newEmail = payload.get("email");
            Users currentUser = userService.findEmail(userDetails.getUsername());
            userService.requestEmailChange(currentUser, newEmail);
            return ResponseEntity.ok("Код подтверждения отправлен на почту");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-email-change")
    public ResponseEntity<?> confirmEmailChange(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            Users currentUsers = userService.findEmail(userDetails.getUsername());
            userService.confirmEmailChange(currentUsers, code);
            return ResponseEntity.ok("Почта успешно обновлена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");
            String currentPassword = payload.get("currentPassword");
            Users currentUsers = userService.findEmail(userDetails.getUsername());
            userService.updateUserPassword(currentUsers , newPassword , currentPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{usersId}")
    public ResponseEntity<String> deleteUsers(@PathVariable("usersId") Long id) {
        try {
            userService.deleteUsers(id);
            return new ResponseEntity<>("Пользователь успешно удален", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/roles")
    //@PreAuthorize("hasRole('ADMIN')")// надо тестануть , в беопастность добавил проверку на админа , надо проверить как эта штука работать будет
    public ResponseEntity<?> updateRoles(@PathVariable Long userId,
                                         @RequestBody Set<Role> newRole) {
        try {
            userService.updateRoles(userId, newRole);
            return ResponseEntity.ok("Роли успешно изменены");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/force-logout")
    public ResponseEntity<?> forceLogout(@PathVariable Long userId) {
        Users user = userService.findById(userId);
        userService.expireUserSessions(user.getEmail());
        return ResponseEntity.ok("Сессии пользователя завершены");
    }

    @GetMapping("/search")
    public ResponseEntity<?> findByUsersEmail(@RequestParam String email) {
        try {
            Users users = userService.findEmail(email);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
