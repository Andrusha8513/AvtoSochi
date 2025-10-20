package ru.avtoAra.AvtoSochi.users;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String confirmationCode;
    private boolean enabled  = false;
    private String pendingEmail;
    private String emailChangeCode;


    private LocalDateTime registrationDate;

    private String passwordResetCode;
    private LocalDateTime passwordResetCodeExpiryDate;

    @ElementCollection(targetClass = Role.class , fetch = FetchType.LAZY)
    @CollectionTable(name = "users_role" , joinColumns = @JoinColumn(name = "users_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public Users(Long id,
                 String email,
                 String password){
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public Users(){
    }

    public void setEmail(String phone){
        this.email = phone;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail(){
        return email;}

    public String getPassword(){
        return password;
    }

    public Long getId(){
        return id;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

  public void setRegistrationDate(LocalDateTime registrationDate){
        this.registrationDate = registrationDate;
    }
    public LocalDateTime getRegistrationDate(){
        return registrationDate;
    }

    public String getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(String passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public LocalDateTime getPasswordResetCodeExpiryDate() {
        return passwordResetCodeExpiryDate;
    }

    public void setPasswordResetCodeExpiryDate(LocalDateTime passwordResetCodeExpiryDate) {
        this.passwordResetCodeExpiryDate = passwordResetCodeExpiryDate;
    }
    public void setEmailChangeCode(String emailChangeCode){
        this.emailChangeCode = emailChangeCode;
    }

    public String getEmailChangeCode(){
        return emailChangeCode;
    }

    public String getPendingEmail() {
        return pendingEmail;
    }

    public void setPendingEmail(String pendingEmail) {
        this.pendingEmail = pendingEmail;
    }

    @PrePersist
    public void prePersist(){
        this.registrationDate = LocalDateTime.now();
    }
}