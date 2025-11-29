package thanhcom.site.lkdt.entity;

import io.swagger.v3.oas.models.Components;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private BigDecimal price; // giá tại thời điểm order

    private BigDecimal total; // quantity * price (Postgres auto generate)

    // N item → 1 order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    // N item → 1 component
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private Components component;

    // getters/setters
}
