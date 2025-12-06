package thanhcom.site.lkdt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "order_history")
@Data
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với Orders
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(nullable = false)
    private String action; // CREATED, UPDATED, DELETED, CHANGE_STATUS, ...

    @Column(columnDefinition = "TEXT")
    private String description; // mô tả chi tiết thay đổi

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Người thực hiện (optional)
    private String updatedBy;
}

