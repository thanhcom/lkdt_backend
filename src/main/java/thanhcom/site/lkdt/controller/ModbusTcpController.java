package thanhcom.site.lkdt.controller;

import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.service.ModbusTcpService;

@RestController
@RequestMapping("/modbus")
public class ModbusTcpController {

    private final ModbusTcpService modbusService;

    public ModbusTcpController(ModbusTcpService modbusService) {
        this.modbusService = modbusService;
    }

    /**
     * Test đọc holding registers
     * Example: GET /modbus/read?unitId=1&address=0&quantity=10
     */
    @GetMapping("/read")
    public int[] readHoldingRegisters(
            @RequestParam int unitId,
            @RequestParam int address,
            @RequestParam int quantity
    ) throws Exception {
        return modbusService.readHoldingRegistersGeneric(unitId, address, quantity);
    }

    /**
     * Test send raw PDU (hex)
     * Example: GET /modbus/sendRaw?unitId=1&address=0&quantity=10
     */
    @GetMapping("/sendRaw")
    public byte[] sendRaw(
            @RequestParam int unitId,
            @RequestParam int address,
            @RequestParam int quantity
    ) throws Exception {
        // Tạo request
        var request = new com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest(address, quantity);
        return modbusService.sendRaw(unitId, request);
    }
}
