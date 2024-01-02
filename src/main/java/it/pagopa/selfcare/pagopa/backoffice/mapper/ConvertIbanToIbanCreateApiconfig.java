package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.OffsetDateTime;

public class ConvertIbanToIbanCreateApiconfig implements Converter<Iban, IbanCreateApiconfig> {

    @Override
    public IbanCreateApiconfig convert(MappingContext<Iban, IbanCreateApiconfig> context) {
        Iban src = context.getSource();
        return IbanCreateApiconfig.builder()
                .description(src.getDescription())
                .iban(src.getIban())
                .active(src.isActive())
                .labels(src.getLabels())
                .validityDate(OffsetDateTime.parse(src.getValidityDate()))
                .dueDate(OffsetDateTime.parse(src.getDueDate()))
                .build();
    }
}
