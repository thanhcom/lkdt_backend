package thanhcom.site.lkdt.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrCode {
    //--------USER
    USER_USERNAME_SIZE(1001,"User must be at least {min}character !!!", HttpStatus.LENGTH_REQUIRED),
    USER_PASSWORD_SIZE(1001,"Password must be at least {min} character !!!",HttpStatus.LENGTH_REQUIRED),
    USER_VALIDATION_FORMAT(1002,"User not validation format !!!",HttpStatus.BAD_REQUEST),
    USER_VALIDATION_PHONE_FORMAT(1002,"Phone number not validation format at least {min} number!!!",HttpStatus.LENGTH_REQUIRED),
    USER_EXISTED(1003,"User existed!!!",HttpStatus.NOT_ACCEPTABLE),
    USER_PASSWORD_NOT_MATH(1004,"Password not math!!!",HttpStatus.UNAUTHORIZED),
    USER_NOT_EXISTED(1004,"User not existed!!!",HttpStatus.NOT_FOUND),
    USER_VIEW_INFO(1004,"Users can only view their own information !!!",HttpStatus.NON_AUTHORITATIVE_INFORMATION),
    USER_ACCESS_DENIED(1003, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),

    //--------EMAIL
    EMAIL_NOTFOUND(1101,"Email Not Found !!!",HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1102,"Email existed !!!",HttpStatus.NOT_ACCEPTABLE),
    EMAIL_INVALID(1103,"Email invalid format !!!",HttpStatus.NOT_ACCEPTABLE),
    //--------TYPE PRODUCT
    TYPE_NOTFOUND(1301,"Type Not Found !!!",HttpStatus.NOT_FOUND),

    //------Token
    TOKEN_EXPIRED(1501,"Token Expired !!!",HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1502,"Token Invalid !!!",HttpStatus.UNAUTHORIZED),

    //------PERMISSION
    PERMISSION_NOTFOUND(1401,"Permission Not Found !!!",HttpStatus.NOT_FOUND),

    //------COMPONENT
    COMPONENT_NOTFOUND(1201,"Component Not Found !!!",HttpStatus.LENGTH_REQUIRED),
    COMPONENT_NAME_BLANK(1001,"Component name not blank", HttpStatus.LENGTH_REQUIRED),
    COMPONENT_NAME_SIZE(1001,"Component type must be at {min} character and  {max} character!!! ", HttpStatus.LENGTH_REQUIRED),
    //------SUPPLIER
    SUPPLIER_NOTFOUND(1601,"Supplier Not Found !!!",HttpStatus.NOT_FOUND),

    //------PROJECT
    PROJECT_NOT_FOUND(1701,"Project Not Found !!!",HttpStatus.NOT_FOUND),

    //------TRANSACTION
    TRANSACTION_NOT_FOUND(1801,"Transaction Not Found !!!",HttpStatus.NOT_FOUND),

    //------SUPPLIER
    SUPPLIER_NOT_FOUND(1601,"Supplier Not Found !!!",HttpStatus.NOT_FOUND),
    //------ROLE
    ROLE_NOTFOUND(1402,"Role Not Found !!!",HttpStatus.NOT_FOUND),

    //------MODBUS TCP
    MODBUS_CONNECTION_FAILED(2001,"Modbus TCP Connection Failed !!!",HttpStatus.SERVICE_UNAVAILABLE),
    MODBUS_READ_ERROR(2002,"Modbus TCP Read Error !!!",HttpStatus.BAD_GATEWAY),
    MODBUS_WRITE_ERROR(2003,"Modbus TCP Write Error !!!",HttpStatus.BAD_GATEWAY),


    UNAUTHORIZED(9999,"Warning !!! UNAUTHORIZED",HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(9998,"Warning !!! UNAUTHENTICATED",HttpStatus.FORBIDDEN),
    INVALID_KEY(9999,"Warning !!! Check and Changer field message valid in ENUM define ",HttpStatus.NOT_ACCEPTABLE),
    //--------Upload File
    CREATE_COMPONENT_SCHEMATIC_FAIL(2100,"Create Component Schematic Fail !!!",HttpStatus.INTERNAL_SERVER_ERROR),
    UPDATE_COMPONENT_SCHEMATIC_FAIL(2101,"Update Component Schematic Fail !!!",HttpStatus.INTERNAL_SERVER_ERROR),
    COMPONENT_SCHEMATIC_NOTFOUND(2102,"Component Schematic Not Found !!!",HttpStatus.NOT_FOUND);

    private final int code;
    private final String Message;
    private final HttpStatus statusCode;

    ErrCode(int code, String message , HttpStatus statusCode) {
        this.code = code;
        this.Message = message;
        this.statusCode = statusCode;
    }

}