package thanhcom.site.lkdt.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.Supplier;
import thanhcom.site.lkdt.repository.SupplierRepository;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SupplierService {
    SupplierRepository supplierRepository;

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        // Logic to create and save a new supplier
        supplierRepository.save(supplier);
        return supplier;
    }

    public Supplier getSupplierById(Long id) {
        // Logic to retrieve a supplier by ID
        return supplierRepository.findById(id).orElse(null);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        // Logic to update an existing supplier
        return supplierRepository.findById(id).map(supplier -> {
            supplier.setName(updatedSupplier.getName());
            supplier.setAddress(updatedSupplier.getAddress());
            supplier.setPhone(updatedSupplier.getPhone());
            supplier.setEmail(updatedSupplier.getEmail());
            supplier.setContact(updatedSupplier.getContact());
            return supplierRepository.save(supplier);
        }).orElse(null);
    }

    public void deleteSupplier(Long id) {
        // Logic to delete a supplier by ID
        supplierRepository.deleteById(id);
    }

}
