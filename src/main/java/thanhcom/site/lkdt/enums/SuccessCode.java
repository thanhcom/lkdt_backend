package thanhcom.site.lkdt.enums;

import lombok.Getter;

@Getter
public enum SuccessCode {

    PRODUCT_FIND_OKE(2202,"Product Find Success !!!"),
    PRODUCT_CREATE_OKE(2201,"Product Create Success !!!"),


    USER_FIND_OKE(1001,"USER Find Success !!!"),
    USER_CREATE_OKE(1101,"USER CREATED OK !!!"),
    USER_UPDATE_OKE(1102,"USER UPDATED OK !!!"),
    USER_DELETE_OKE(1103,"USER DELETED OK !!!"),
    USER_LOGIN_OKE(1200,"USER Login Success !!!"),


    TOKEN_CHECK_OK(1300,"Token Check Success !!!");
    ;
    private final int code;
    private final String Message;

    SuccessCode(int code, String message) {
        this.code = code;
        Message = message;
    }

}