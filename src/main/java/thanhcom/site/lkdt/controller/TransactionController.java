package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.TransactionDto;
import thanhcom.site.lkdt.entity.Transaction;
import thanhcom.site.lkdt.mapper.TransactionMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.TransactionService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/transaction")
public class TransactionController {
    TransactionService transactionService;
    TransactionMapper transactionMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions() {
    List<Transaction> allTransactions = transactionService.getAllTransactions();
    ResponseApi<List<TransactionDto>> responseApi = new ResponseApi<>();
    responseApi.setData(transactionMapper.ToEntityList(allTransactions));
    responseApi.setResponseCode(2000);
    responseApi.setMessenger("Lấy tất cả giao dịch thành công");
    return ResponseEntity.ok(responseApi);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<TransactionDto>> searchTransactions(
            @RequestParam(required = false) Long componentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String componentName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 🔍 Gọi service thực hiện tìm kiếm
        Page<Transaction> transactions = transactionService.searchTransactions(
                componentId, projectId, componentName, projectName, type, start, end, page, size
        );

        // ✅ Convert Page<Entity> → Page<DTO>
        Page<TransactionDto> transactionDtos = transactions.map(transactionMapper::ToEntity);

        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionsById(@PathVariable Long id) {
        Transaction transactions = transactionService.getTransactionById(id);
        ResponseApi<TransactionDto> responseApi = new ResponseApi<>();
        responseApi.setData(transactionMapper.ToEntity(transactions));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy giao dịch theo Transactions ID thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/component/{componentId}")
    public ResponseEntity<?> getTransactionsByComponentId(@PathVariable Long componentId) {
        List<Transaction> transactions = transactionService.getTransactionsByComponentId(componentId);
        ResponseApi<List<TransactionDto>> responseApi = new ResponseApi<>();
        responseApi.setData(transactionMapper.ToEntityList(transactions));
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Lấy giao dịch theo Component ID thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getTransactionsByProjectId(@PathVariable Long projectId) {
        List<Transaction> transactions = transactionService.getTransactionsByProjectId(projectId);
        ResponseApi<List<TransactionDto>> responseApi = new ResponseApi<>();
        responseApi.setData(transactionMapper.ToEntityList(transactions));
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Lấy giao dịch theo Project ID thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.ToDto(transactionDto);
        Transaction savedTransaction = transactionService.saveTransaction(transaction);
        ResponseApi<TransactionDto> responseApi = new ResponseApi<>();
        responseApi.setData(transactionMapper.ToEntity(savedTransaction));
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Tạo giao dịch thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto) {
        Transaction updatedTransactionData = transactionMapper.ToDto(transactionDto);
        Transaction updatedTransaction = transactionService.editTransactionById(id, updatedTransactionData);
        ResponseApi<TransactionDto> responseApi = new ResponseApi<>();
        responseApi.setData(transactionMapper.ToEntity(updatedTransaction));
        responseApi.setResponseCode(2005);
        responseApi.setMessenger("Cập nhật giao dịch thành công");
        return ResponseEntity.ok(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        ResponseApi<Void> responseApi = new ResponseApi<>();
        responseApi.setData(null);
        responseApi.setResponseCode(2006);
        responseApi.setMessenger("Xóa giao dịch thành công");
        return ResponseEntity.ok(responseApi);
    }

}
