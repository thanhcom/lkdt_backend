package thanhcom.site.lkdt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "components")
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "specification", length = Integer.MAX_VALUE)
    private String specification;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "package", length = 50)
    private String packageField;

    @Column(name = "unit", length = 20)
    private String unit;

    @ColumnDefault("0")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "location", length = 100)
    private String location;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}