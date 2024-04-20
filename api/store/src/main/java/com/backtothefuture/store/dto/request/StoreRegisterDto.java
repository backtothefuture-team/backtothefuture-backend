package com.backtothefuture.store.dto.request;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.store.annotation.NumericStringList;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "가게 등록을 위한 데이터 전송 객체(DTO)")
@Builder
public record StoreRegisterDto(
        @NotBlank(message = "가게이름은 필수 항목입니다.")
        @Size(max = 20, message = "가게이름은 최대 20자 이하로 입력해주세요.")
        @Schema(description = "가게 이름", example = "베이커리 카페", required = true)
        String name,

        @NotBlank(message = "가게설명은 필수 항목입니다.")
        @Size(max = 50, message = "가게설명은 최대 50자 이하로 입력해주세요.")
        @Schema(description = "가게 설명", example = "신선한 빵과 커피를 제공하는 카페", required = true)
        String description,

        @NotBlank(message = "가게위치는 필수 항목입니다.")
        @Size(max = 50, message = "가게위치는 최대 50자 이하로 입력해주세요.")
        @Schema(description = "가게 위치", example = "서울시 강남구 역삼동 123-45", required = true)
        String location,

        @NumericStringList(message = "연락처는 숫자만 입력할 수 있습니다.")
        @ArraySchema(arraySchema = @Schema(description = "가게 연락처", example = "[\"010\", \"0000\", \"0000\"]"))
        List<String> contact,

        @DateTimeFormat(pattern = "HH:mm")
        @NotNull(message = "오픈 시간은 필수 입니다.")
        @Schema(description = "오픈 시간", example = "09:00", required = true)
        LocalTime startTime,

        @DateTimeFormat(pattern = "HH:mm")
        @NotNull(message = "마감 시간은 필수 입니다.")
        @Schema(description = "마감 시간", example = "22:00", required = true)
        LocalTime endTime
) {

    public static Store toEntity(StoreRegisterDto storeRegisterDto, Member member) {
        String contact = Optional.ofNullable(storeRegisterDto.contact())
                .map(num -> String.join("-", num))
                .orElse("");

        return Store.builder()
                .name(storeRegisterDto.name())
                .description(storeRegisterDto.description())
                .location(storeRegisterDto.location())
                .contact(contact)
                .member(member)
                .startTime(storeRegisterDto.startTime())
                .endTime(storeRegisterDto.endTime())
                .build();
    }


}
