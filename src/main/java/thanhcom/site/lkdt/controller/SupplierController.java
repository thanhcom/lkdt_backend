package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.SupplierDto;
import thanhcom.site.lkdt.entity.Supplier;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.mapper.SupplierMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.responseApi.ResponsePage;
import thanhcom.site.lkdt.service.SupplierService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/supplier")
public class SupplierController {
    SupplierService supplierService;
    SupplierMapper supplierMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSuppliers(){
        List<Supplier> supplierList = supplierService.GetSuppliers();
        ResponseApi<List<SupplierDto>> responseApi = new ResponseApi<>();
        responseApi.setData(supplierMapper.ResToDtoList(supplierList));
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Get all suppliers successfully");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSupplier(@RequestBody SupplierDto supplierResponse) {
        Supplier supplier = supplierMapper.DtoToRes(supplierResponse);
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        ResponseApi<SupplierDto> responseApi = new ResponseApi<>();
        responseApi.setData(supplierMapper.ResToDto(createdSupplier));
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Supplier created successfully");
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id,@RequestBody SupplierDto supplierResponse) {
        Supplier updatedSupplier = supplierMapper.DtoToRes(supplierResponse);
        Supplier supplier = supplierService.updateSupplier(id, updatedSupplier);
        ResponseApi<SupplierDto> responseApi = new ResponseApi<>();
        responseApi.setData(supplierMapper.ResToDto(supplier));
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Supplier updated successfully");
        return ResponseEntity.ok(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        Supplier supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            throw new AppException(ErrCode.SUPPLIER_NOT_FOUND);
        }
        supplierService.deleteSupplier(id);
        ResponseApi<Void> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Supplier deleted successfully");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        ResponseApi<SupplierDto> responseApi = new ResponseApi<>();
        if (supplier == null) {
            throw new AppException(ErrCode.SUPPLIER_NOT_FOUND);
        }
        responseApi.setData(supplierMapper.ResToDto(supplier));
        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Get supplier by ID successfully");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSupplier(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            Pageable pageable
    ) {
        ResponseApi<List<?>> responseApi = new ResponseApi<>();
        Page<Supplier> page = supplierService.searchSuppliers(keyword, id, pageable);
        // ✅ Convert Page<Entity> → Page<DTO>
        Page<SupplierDto> supplierResponses = page.map(supplierMapper::ResToDto);
        responseApi.setData(supplierResponses.getContent());
        responseApi.setPageInfo(ResponsePage.builder()
                .currentPage(page.getNumber()+1)
                .pageSize(page.getSize())
                .totalPage(page.getTotalPages())
                .totalElement(page.getTotalElements())
                .isEmpty(page.isEmpty())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hashCode(page.hashCode())
                .sortInfo(page.getSort().toString())
                .hasNext(page.hasNext())
                .hasContent(page.hasContent())
                .hasPrevious(page.hasPrevious())
                .build()
        );
        return ResponseEntity.ok(responseApi);
    }
}
