package thanhcom.site.lkdt.responseApi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Xoá Null Trong Dữ Liệu Trả Về
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApi<T>{
    int ResponseCode = 1001;
    T data;
    String Messenger;
    String Author = "Copyright 2025 thanhtrang.online";
    //Khi Nao Tra Ve page moi dung den
    ResponsePage pageInfo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp = LocalDateTime.now();
}