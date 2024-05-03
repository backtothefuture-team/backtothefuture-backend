package com.backtothefuture.domain.store;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    private String name;        // 가게 이름

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // 가게 설명

    private String location;    // 가게 위치

    private String contact;        // 연락처

    private String image;        // 가게 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;        // 회원

    private double averageRating; // TODO 리뷰가 등록될 때 rating, ratingCount가 업데이트되어야 함

    private int totalRatingCount;

    private LocalTime startTime; // 영업 시작 시간

    private LocalTime endTime; // 영업 종료 시간

    public void setThumbnailUrl(String url) {
        this.image = url;
    }
}
