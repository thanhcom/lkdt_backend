package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.TransactionDto;
import thanhcom.site.lkdt.entity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction ToDto(TransactionDto transactionDto);
    TransactionDto ToEntity(Transaction transaction);

    List<Transaction> ToDtoList(List<TransactionDto> transactionDtos);
    List<TransactionDto> ToEntityList(List<Transaction> transactions);
}
