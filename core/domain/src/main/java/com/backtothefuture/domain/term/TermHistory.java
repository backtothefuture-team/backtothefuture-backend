package com.backtothefuture.domain.term;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "member_id"),
        @UniqueConstraint(columnNames = "term_id")
})
public class TermHistory extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_history_id")
    private Long id;

    private boolean isAccepted; // 약관 동의 여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term; // 약관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
