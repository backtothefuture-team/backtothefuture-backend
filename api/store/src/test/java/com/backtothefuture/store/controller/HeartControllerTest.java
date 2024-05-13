package com.backtothefuture.store.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.backtothefuture.global.RestAssuredTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class HeartControllerTest extends RestAssuredTest {

    @Test
    public void testAddHeartToStore() {
        given()
                .auth().preemptive().basic("user1", "password") // 사용자 인증 정보, 필요한 경우 적절히 변경
                .contentType(ContentType.JSON)
                .when()
                .post("/hearts/stores/{storeId}", 1)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("정상 처리되었습니다."))
                .body("data", equalTo("SUCCESS"));
    }

    @Test
    public void testRemoveHeartFromStore() {
        given()
                .auth().preemptive().basic("user1", "password") // 사용자 인증 정보, 필요한 경우 적절히 변경
                .contentType(ContentType.JSON)
                .when()
                .delete("/hearts/stores/{storeId}", 1)
                .then()
                .statusCode(204); // 찜 취소 성공을 의미하는 HTTP 상태 코드 204 검증
    }

}
