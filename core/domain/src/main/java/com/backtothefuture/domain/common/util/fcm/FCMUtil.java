package com.backtothefuture.domain.common.util.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FCMUtil {

    private static final String REGISTER_TITLE = "주문 접수";
    private static final String REGISTER_BODY = "주문이 접수되었습니다.";
    private static final String REMIND_TITLE = "픽업 30분 전 알림";
    private static final String REMIND_BODY = "픽업 30분 전입니다.";
    private final FCMAsyncUtil fcmAsyncUtil;

    /**
     * 주문 접수 알림 전송
     */
    public void sendReservationRegisterMessage(String token) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(REGISTER_TITLE)
                        .setBody(REGISTER_BODY)
                        .build())
                .build();
        fcmAsyncUtil.sendReservationRegisterMessage(message);
    }

    /**
     * 픽업 30분 전 알림 전송
     */
    public void sendReservationRemindMessages(List<String> tokens) throws FirebaseMessagingException {

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(REMIND_TITLE)
                        .setBody(REMIND_BODY)
                        .build())
                .addAllTokens(tokens)
                .build();

        fcmAsyncUtil.sendReservationRemindMessages(multicastMessage);
    }

}
