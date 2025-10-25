package thanhcom.site.lkdt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ComponentSupplierId implements Serializable {
    private static final long serialVersionUID = -3756161670109554391L;
    @Column(name = "component_id", nullable = false)
    private Long componentId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ComponentSupplierId entity = (ComponentSupplierId) o;
        return Objects.equals(this.componentId, entity.componentId) &&
                Objects.equals(this.supplierId, entity.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, supplierId);
    }

}