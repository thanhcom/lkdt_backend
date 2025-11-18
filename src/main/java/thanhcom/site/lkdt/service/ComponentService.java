package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thanhcom.site.lkdt.dto.ComponentDetail;
import thanhcom.site.lkdt.dto.request.ComponentCreateRequest;
import thanhcom.site.lkdt.dto.request.SupplierPriceRequest;
import thanhcom.site.lkdt.dto.response.ComponentSupplierResponse;
import thanhcom.site.lkdt.dto.SupplierDto;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.ComponentSupplier;
import thanhcom.site.lkdt.entity.ComponentSupplierId;
import thanhcom.site.lkdt.entity.Supplier;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.mapper.ComponentMapper;
import thanhcom.site.lkdt.mapper.ComponentSupplierMapper;
import thanhcom.site.lkdt.mapper.SupplierMapper;
import thanhcom.site.lkdt.mapper.SupplierPriceMapper;
import thanhcom.site.lkdt.repository.ComponentRepository;
import thanhcom.site.lkdt.repository.ComponentSupplierRepository;
import thanhcom.site.lkdt.repository.SupplierRepository;
import thanhcom.site.lkdt.specification.ComponentSpecification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ComponentService {
     ComponentSupplierMapper componentSupplierMapper;
     ComponentRepository componentRepository;
     ComponentSupplierRepository componentSupplierRepository;
     SupplierRepository supplierRepository;



    // 🔹 Lấy tất cả component
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    // 🔹 Tìm kiếm nhiều điều kiện với phân trang
    public Page<Component> searchComponents(String keyword, Long id, Integer stockQuantity, Pageable pageable) {
        return componentRepository.findAll(
                ComponentSpecification.filter(keyword, id, stockQuantity),
                pageable
        );
    }

    // 🔹 Lọc theo type
    public List<Component> getComponentsByType(String type) {
        return componentRepository.findAll().stream()
                .filter(c -> c.getType() != null && c.getType().equalsIgnoreCase(type))
                .toList();
    }

    // 🔹 Lọc theo hãng sản xuất
    public List<Component> getComponentsByManufacturer(String manufacturer) {
        return componentRepository.findAll().stream()
                .filter(c -> c.getManufacturer() != null && c.getManufacturer().equalsIgnoreCase(manufacturer))
                .toList();
    }

    // 🔹 Lọc theo nhà cung cấp
    public List<Component> getComponentsBySupplier(Long supplierId) {
        List<ComponentSupplier> componentSuppliers = componentSupplierRepository.findAllBySupplierId(supplierId);
        List<Component> components = new ArrayList<>();
        for (ComponentSupplier cs : componentSuppliers) {
            components.add(cs.getComponent());
        }
        if (components.isEmpty()) {
            throw new AppException(ErrCode.COMPONENT_NOTFOUND);
        }
        return components;
    }

    // 🔹 Lọc theo tên
    public List<Component> getComponentsByName(String name) {
        return componentRepository.findAll().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
                .toList();
    }

    // 🔹 Lấy chi tiết theo ID
    public Component getComponentById(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
    }

    // 🔹 Thêm mới component
    public Component createComponent(ComponentCreateRequest request) {
        List<ComponentSupplierResponse> supplierList = new ArrayList<>();
        Component component = new Component();
        component.setName(request.getName());
        component.setType(request.getType());
        component.setManufacturer(request.getManufacturer());
        component.setUnit(request.getUnit());
        component.setLocation(request.getLocation());
        component.setSpecification(request.getSpecification());
        component.setStockQuantity(request.getStockQuantity());
        component.setCreatedAt(OffsetDateTime.now());

        componentRepository.save(component);

        if (request.getSuppliers() != null) {
            for (SupplierPriceRequest s : request.getSuppliers()) {
                Supplier supplier = supplierRepository.findById(s.getSupplierId())
                        .orElseThrow(() -> new AppException(ErrCode.SUPPLIER_NOTFOUND));

                ComponentSupplier cs = new ComponentSupplier();
                cs.setId(new ComponentSupplierId(component.getId(), supplier.getId()));
                cs.setComponent(component);
                cs.setSupplier(supplier);
                cs.setPrice(s.getPrice());
                cs.setLeadTime(s.getLeadTime());
                componentSupplierRepository.save(cs);
            }
        }
        return component;
    }

    // 🔹 Cập nhật component + supplier
    public Component updateComponent(Long id, ComponentCreateRequest request) {
        Component component = componentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));

        component.setName(request.getName());
        component.setType(request.getType());
        component.setManufacturer(request.getManufacturer());
        component.setUnit(request.getUnit());
        component.setLocation(request.getLocation());
        component.setSpecification(request.getSpecification());
        component.setStockQuantity(request.getStockQuantity());

        // ⚡ Xóa các supplier cũ theo componentId (chạy query SQL, không load toàn bảng)
        componentSupplierRepository.deleteByComponentId(id);

        // ⚡ Tạo lại supplier mới
        if (request.getSuppliers() != null) {
            for (SupplierPriceRequest s : request.getSuppliers()) {
                Supplier supplier = supplierRepository.findById(s.getSupplierId())
                        .orElseThrow(() -> new AppException(ErrCode.SUPPLIER_NOTFOUND));

                ComponentSupplier cs = new ComponentSupplier();
                cs.setId(new ComponentSupplierId(component.getId(), supplier.getId()));
                cs.setComponent(component);
                cs.setSupplier(supplier);
                cs.setPrice(s.getPrice());
                cs.setLeadTime(s.getLeadTime());
                componentSupplierRepository.save(cs);
            }
        }

        return componentRepository.save(component);
    }

    // 🔹 Xóa component
    public void deleteComponent(Long id) {
        Component component = componentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));

        // ⚡ Xóa nhanh qua custom query
        componentSupplierRepository.deleteByComponentId(id);

        componentRepository.delete(component);
    }

    public ComponentDetail getComponentDetail(Long id) {
        Component component = getComponentById(id);
        List<SupplierDto> suppliers = new ArrayList<>();
        List<ComponentSupplierResponse> componentSuppliers = componentSupplierMapper.ResToEntityList(componentSupplierRepository.findAllByComponentId(id));
        componentSuppliers.forEach(item -> {
            suppliers.add(item.getSupplier());
        });

        return ComponentDetail.builder()
                .name(component.getName())
                .type(component.getType())
                .specification(component.getSpecification())
                .manufacturer(component.getManufacturer())
                .packageField(component.getPackageField())
                .unit(component.getUnit())
                .stockQuantity(component.getStockQuantity())
                .location(component.getLocation())
                .createdAt(component.getCreatedAt())
                .componentSuppliers(componentSuppliers)
                .build();
    }


}
