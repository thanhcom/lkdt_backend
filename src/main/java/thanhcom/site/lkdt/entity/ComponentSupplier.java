package thanhcom.site.lkdt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "component_supplier")
public class ComponentSupplier {
    @EmbeddedId
    private ComponentSupplierId id;

    @MapsId("componentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "component_id", nullable = false)
    @JsonIgnore
    private Component component;

    @MapsId("supplierId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore
    private Supplier supplier;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "lead_time")
    private Integer leadTime;

}