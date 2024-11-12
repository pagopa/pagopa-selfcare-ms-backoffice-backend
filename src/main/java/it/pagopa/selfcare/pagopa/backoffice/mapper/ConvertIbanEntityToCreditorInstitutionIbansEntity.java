package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanEntity;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link IbanEntity} instance to a {@link CreditorInstitutionIbansEntity} instance
 */
public class ConvertIbanEntityToCreditorInstitutionIbansEntity implements Converter<IbanEntity, CreditorInstitutionIbansEntity> {

    @Override
    public CreditorInstitutionIbansEntity convert(MappingContext<IbanEntity, CreditorInstitutionIbansEntity> context) {
        IbanEntity model = context.getSource();

        return CreditorInstitutionIbansEntity.builder()
                .id(model.getIban())
                .iban(model.getIban())
                .label(model.getLabel())
                .ciName(model.getCiName())
                .status(model.getStatus())
                .validityDate(model.getValidityDate())
                .description(model.getDescription())
                .ciFiscalCode(model.getCiFiscalCode())
                .build();
    }
}