package thanhcom.site.lkdt.service;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.ComponentSupplier;
import thanhcom.site.lkdt.entity.Supplier;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.ComponentRepository;
import thanhcom.site.lkdt.repository.ComponentSupplierRepository;
import thanhcom.site.lkdt.repository.SupplierRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ComponentSupplierService {
    ComponentSupplierRepository componentSupplierRepository;
    ComponentRepository componentRepository;
    SupplierRepository supplierRepository;

    @Transactional
    public ComponentSupplier create(Long componentId, Long supplierId, BigDecimal price, Integer leadTime) {
        Component component = componentRepository.findById(componentId)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new AppException(ErrCode.SUPPLIER_NOTFOUND));

        ComponentSupplier cs = new ComponentSupplier();
        cs.setComponent(component);
        cs.setSupplier(supplier);
        cs.setPrice(price);
        cs.setLeadTime(leadTime);
        return componentSupplierRepository.save(cs);
    }

    public ComponentSupplier getComponentSupplierById(Long id) {
        return componentSupplierRepository.findById(id).orElse(null);
    }

    public List<ComponentSupplier> getComponentSuppliersByComponentId(Long componentId) {
        return componentSupplierRepository.findAllByComponentId(componentId);
    }


    public void deleteComponentSupplierById(Long id) {
        componentSupplierRepository.deleteById(id);
    }


}
