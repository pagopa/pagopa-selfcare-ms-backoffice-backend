package it.pagopa.selfcare.pagopa.backoffice.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class LocalDateToOffset implements Converter<LocalDate, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(MappingContext<LocalDate, OffsetDateTime> context) {
        LocalDate localDate = context.getSource();
        return OffsetDateTime.of(localDate, LocalTime.MIDNIGHT, ZoneOffset.UTC);
    }
}
