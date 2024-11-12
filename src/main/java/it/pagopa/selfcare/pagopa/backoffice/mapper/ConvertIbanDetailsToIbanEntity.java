package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.entity.IbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanLabel;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Converter class that specify how to convert a {@link IbanDetails} instance to a {@link IbanEntity} instance
 */
public class ConvertIbanDetailsToIbanEntity implements Converter<IbanDetails, IbanEntity> {

    @Override
    public IbanEntity convert(MappingContext<IbanDetails, IbanEntity> context) {
        IbanDetails model = context.getSource();

        return IbanEntity.builder()
                .ciName(model.getCiName())
                .ciFiscalCode(model.getCiFiscalCode())
                .iban(model.getIban())
                .status(OffsetDateTime.now().isAfter(model.getValidityDate()) ? "ATTIVO" : "DISATTIVO")
                .validityDate(model.getValidityDate().toInstant())
                .description(model.getDescription())
                .label(model.getLabels().parallelStream()
                        .map(IbanLabel::getName)
                        .collect(Collectors.joining(" - ")))
                .build();
    }
}