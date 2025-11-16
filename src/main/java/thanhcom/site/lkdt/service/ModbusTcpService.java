package thanhcom.site.lkdt.service;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.ModbusPduSerializer.DefaultRequestSerializer;
import com.digitalpetri.modbus.internal.util.Hex;
import com.digitalpetri.modbus.tcp.Netty;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import com.digitalpetri.modbus.tcp.client.NettyTimeoutScheduler;
import io.netty.buffer.ByteBuf;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Service
public class ModbusTcpService {

    private ModbusTcpClient client;
    private NettyTcpClientTransport transport;
    private final String HOST = "192.168.1.19";
    private final int PORT = 502;

    @PostConstruct
    public void init() {
        transport = NettyTcpClientTransport.create(cfg -> {
            cfg.hostname = HOST;
            cfg.port = PORT;
        });

        transport.addConnectionListener(new NettyTcpClientTransport.ConnectionListener() {
            @Override
            public void onConnection() {
                System.out.println("✅ Modbus client connected!");
            }

            @Override
            public void onConnectionLost() {
                System.out.println("⚠️ Modbus client lost connection!");
            }
        });

        client = ModbusTcpClient.create(
                transport,
                cfg -> cfg.timeoutScheduler = new NettyTimeoutScheduler(Netty.sharedWheelTimer())
        );

        // Không connect trực tiếp lúc init, để tránh app crash nếu Modbus chưa bật
        System.out.println("⚠️ Modbus client chưa connect, sẽ retry khi gọi read/write");
    }

    /**
     * Connect nếu chưa connect
     */
    private synchronized void connectIfNeeded() {
        if (client.isConnected()) return;
        try {
            client.connect();
            System.out.println("✅ Modbus client connected (lazy)!");
        } catch (Exception e) {
            System.out.println("⚠️ Không thể connect Modbus: " + e.getMessage());
        }
    }

    /**
     * Read holding registers generic, retry connect nếu cần
     */
    public int[] readHoldingRegistersGeneric(int unitId, int address, int quantity) throws Exception {
        connectIfNeeded(); // lazy connect

        Object rsp = client.readHoldingRegisters(unitId, new ReadHoldingRegistersRequest(address, quantity));
        byte[] rawBytes = tryExtractBytesFromResponse(rsp);
        if (rawBytes == null) {
            throw new IllegalStateException("Không thể trích dữ liệu thanh ghi từ response (response class: "
                    + (rsp != null ? rsp.getClass().getName() : "null") + ")");
        }

        int availableRegs = rawBytes.length / 2;
        int useQty = Math.min(quantity, availableRegs);

        int[] regs = new int[useQty];
        for (int i = 0; i < useQty; i++) {
            int hi = rawBytes[i * 2] & 0xFF;
            int lo = rawBytes[i * 2 + 1] & 0xFF;
            regs[i] = (hi << 8) | lo;
        }

        if (useQty < quantity) {
            regs = Arrays.copyOf(regs, quantity);
        }

        return regs;
    }

    private byte[] tryExtractBytesFromResponse(Object rsp) {
        if (rsp == null) return null;

        Class<?> cls = rsp.getClass();
        try {
            Method m = cls.getMethod("getRegisters");
            Object val = m.invoke(rsp);
            if (val != null) return extractBytes(val);
        } catch (Exception ignored) {}

        try {
            Field f = cls.getDeclaredField("registers");
            f.setAccessible(true);
            Object val = f.get(rsp);
            if (val != null) return extractBytes(val);
        } catch (Exception ignored) {}

        try {
            String s = rsp.toString();
            int idx = s.indexOf("registers=");
            if (idx >= 0) {
                String hex = s.substring(idx + "registers=".length()).replaceAll("[\\[\\]]", "").trim();
                if (hex.endsWith("]")) hex = hex.substring(0, hex.length() - 1);
                hex = hex.replaceAll("[^0-9A-Fa-f]", "");
                if (hex.length() % 2 == 0 && !hex.isEmpty()) {
                    byte[] out = new byte[hex.length() / 2];
                    for (int i = 0; i < out.length; i++) {
                        out[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
                    }
                    return out;
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    private byte[] extractBytes(Object val) {
        if (val instanceof ByteBuf buf) {
            int len = buf.readableBytes();
            byte[] out = new byte[len];
            buf.getBytes(buf.readerIndex(), out);
            return out;
        }
        if (val instanceof byte[]) return (byte[]) val;
        if (val instanceof short[] s) {
            byte[] out = new byte[s.length * 2];
            for (int i = 0; i < s.length; i++) {
                out[i * 2] = (byte) ((s[i] >> 8) & 0xFF);
                out[i * 2 + 1] = (byte) (s[i] & 0xFF);
            }
            return out;
        }
        if (val instanceof int[] s) {
            byte[] out = new byte[s.length * 2];
            for (int i = 0; i < s.length; i++) {
                out[i * 2] = (byte) ((s[i] >> 8) & 0xFF);
                out[i * 2 + 1] = (byte) (s[i] & 0xFF);
            }
            return out;
        }
        return null;
    }

    public byte[] sendRaw(int unitId, ReadHoldingRegistersRequest request) throws Exception {
        connectIfNeeded(); // lazy connect

        ByteBuffer buffer = ByteBuffer.allocate(256);
        DefaultRequestSerializer.INSTANCE.encode(request, buffer);
        byte[] pdu = new byte[buffer.position()];
        buffer.flip();
        buffer.get(pdu);

        System.out.println("➡ RAW Request: " + Hex.format(pdu));
        byte[] response = client.sendRaw(unitId, pdu);
        System.out.println("⬅ RAW Response: " + Hex.format(response));
        return response;
    }
}
