package it.pagopa.selfcare.pagopa.backoffice.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class OffsetToLocalDate implements Converter<OffsetDateTime, LocalDate> {

    @Override
    public LocalDate convert(MappingContext<OffsetDateTime, LocalDate> context) {
        OffsetDateTime offsetDateTime = context.getSource();
        return offsetDateTime.toLocalDate();
    }
}
