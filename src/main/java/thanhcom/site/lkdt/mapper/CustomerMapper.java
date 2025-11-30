package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.CustomerDTO;
import thanhcom.site.lkdt.entity.Customer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO toDTO(Customer entity);
    List<CustomerDTO> toDTOs(List<Customer> customerList);

    Customer toEntity(CustomerDTO dto);
    List<Customer> toEntities(List<CustomerDTO> customerDTOList);
}
