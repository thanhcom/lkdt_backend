package thanhcom.site.lkdt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Column(name = "transaction_type", length = 10)
    private String transactionType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("now()")
    @Column(name = "transaction_date")
    private OffsetDateTime transactionDate;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "project_id")
    private Project project;

}