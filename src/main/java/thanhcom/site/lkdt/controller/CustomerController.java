package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.CustomerDTO;
import thanhcom.site.lkdt.entity.Customer;
import thanhcom.site.lkdt.mapper.CustomerMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.responseApi.ResponsePage;
import thanhcom.site.lkdt.service.CustomerService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("customer")
public class CustomerController {
    CustomerService customerService;
    CustomerMapper customerMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllComponents() {
        List<CustomerDTO> customerDTOList = customerMapper.toDTOs(customerService.getAllCustomers());
        ResponseApi<List<CustomerDTO>> responseApi = new ResponseApi<>();
        responseApi.setData(customerDTOList);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy danh sách khách hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchComponent(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            Pageable pageable
    ) {
        ResponseApi<List<?>> responseApi = new ResponseApi<>();
        Page<Customer> page = customerService.searchCustomers(keyword, id, pageable);
        // ✅ Convert Page<Entity> → Page<DTO>
        Page<CustomerDTO> customerDTOS = page.map(customerMapper::toDTO);
        responseApi.setData(customerDTOS.getContent());
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        CustomerDTO customerDTO = customerMapper.toDTO(customer);
        ResponseApi<CustomerDTO> responseApi = new ResponseApi<>();
        responseApi.setData(customerDTO);
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Lấy thông tin khách hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer createdCustomer = customerService.createCustomer(customer);
        CustomerDTO createdCustomerDTO = customerMapper.toDTO(createdCustomer);
        ResponseApi<CustomerDTO> responseApi = new ResponseApi<>();
        responseApi.setData(createdCustomerDTO);
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Tạo mới khách hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        CustomerDTO updatedCustomerDTO = customerMapper.toDTO(updatedCustomer);
        ResponseApi<CustomerDTO> responseApi = new ResponseApi<>();
        responseApi.setData(updatedCustomerDTO);
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Cập nhật khách hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        ResponseApi<Boolean> responseApi = new ResponseApi<>();
        responseApi.setData(deleted);
        responseApi.setResponseCode(2005);
        responseApi.setMessenger("Xóa khách hàng thành công");
        return ResponseEntity.ok(responseApi);
    }
}
