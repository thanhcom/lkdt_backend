package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.request.UserRequest;
import thanhcom.site.lkdt.dto.response.UserResponse;
import thanhcom.site.lkdt.entity.Account;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    UserRequest ReqToDto(Account account);
    List<UserRequest> ListReqToDto(List<Account> accounts);
    Account DtoToReq(UserRequest userRequest);
    List<Account> ListDtoToReq(List<UserRequest> userRequests);

    UserResponse ResToDto(Account account);
    List<UserResponse> ListResToDto(List<Account> accounts);
    Account DtoToRes(UserResponse userResponse);
    List<Account> ListDtoToRes(List<UserResponse> userResponses);
}
