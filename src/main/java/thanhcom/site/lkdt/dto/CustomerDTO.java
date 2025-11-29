package thanhcom.site.lkdt.dto;

import lombok.Data;

@Data
public class CustomerDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
}
