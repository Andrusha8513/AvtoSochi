package ru.avtoAra.AvtoSochi.users;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByPendingEmail(String pendingEmail);

    Optional<Users> findByConfirmationCode(String code);

    List<Users> findByEnabledIsFalseAndRegistrationDateBefore(LocalDateTime dateTime);
}