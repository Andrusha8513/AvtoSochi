package ru.avtoAra.AvtoSochi.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.avtoAra.AvtoSochi.users.Order.OrderStatus;

@Service
public class EmailService {
    private OrderStatus status;
    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Подтверждение регистрации на AvtoSochi");
        message.setText("Работяга твой персональны код)): " + code);
        mailSender.send(message);
    }

    public void sendOrderStatusUpdate(String to,Long orderId,
                                      OrderStatus oldStatus ,
                                      OrderStatus newStatus){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Обновление статуса заказа:" + orderId);

        String newSatisNext = (newStatus != null) ? newStatus.getDisplayName() : "Не определён";
        String oldStatusText = (oldStatus != null) ? oldStatus.getDisplayName() : "Не определён";

        message.setText(
                "Уважаемый клиент!\n\n" +
                        "Статус вашего заказа №" + orderId + " был изменен:\n" +
                        "Было: " + oldStatusText + "\n" +
                        "Стало: " + newSatisNext + "\n\n" +
                        "Спасибо за покупку в нашем магазине!\n" +
                        "AutoSochi"
        );

        mailSender.send(message);
    }

    public void sendPasswordResetCodeEmail(String to , String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Сброс пароля для AvtoSochi");
        message.setText("Ваш код для сброса пароля: " + code + "\n\n" +
                "Это код истечёт через 15 минут");
        mailSender.send(message);
    }
}


