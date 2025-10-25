package thanhcom.site.lkdt.responseApi;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePage {
    int currentPage;
    int totalPage;
    int pageSize;
    long totalElement;
    boolean hasContent;
    boolean hasNext;
    boolean hasPrevious;
    int hashCode;
    boolean isEmpty;
    boolean isFirst;
    boolean isLast;
    String  sortInfo;
}