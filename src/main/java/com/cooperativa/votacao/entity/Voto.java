package com.cooperativa.votacao.entity;

import com.cooperativa.votacao.enums.VotoEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"agenda_id", "associate_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vote_agenda"))
    private Pauta agenda;

    @Column(name = "associate_id", nullable = false)
    private String associateId;

    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote", nullable = false, length = 3)
    private VotoEnum voto;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
