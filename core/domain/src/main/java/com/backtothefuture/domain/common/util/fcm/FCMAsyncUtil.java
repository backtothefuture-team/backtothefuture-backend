package com.backtothefuture.domain.common.util.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMAsyncUtil {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * 예약(주문) 접수 시 알림을 요청한다.
     */
    @Async("FCMAsyncBean")
    public String sendReservationRegisterMessage(Message message) throws FirebaseMessagingException {
        return firebaseMessaging.send(message);
    }

    /**
     * 특정 시각을 기준으로 예약 시간 30분 전 고객들에게 알림을 요청한다.
     */
    @Async("FCMAsyncBean")
    public void sendReservationRemindMessages(MulticastMessage messages) throws FirebaseMessagingException {
        firebaseMessaging.sendEachForMulticast(messages);
    }

}
