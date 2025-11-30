package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public class DateTimeMapper {

    public OffsetDateTime asOffsetDateTime(LocalDateTime local) {
        return local == null ? null : local.atOffset(ZoneOffset.UTC);
    }

    public LocalDateTime asLocalDateTime(OffsetDateTime offset) {
        return offset == null ? null : offset.toLocalDateTime();
    }
}
