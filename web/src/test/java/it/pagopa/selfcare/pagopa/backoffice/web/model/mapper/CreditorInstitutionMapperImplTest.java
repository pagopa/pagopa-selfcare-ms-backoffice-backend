package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static it.pagopa.selfcare.pagopa.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditorInstitutionMapperImplTest {
    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    @Test
    void toCreditorInstitutionDetailsResource(){
        //given
        CreditorInstitutionDetails details = mockInstance(new CreditorInstitutionDetails());
        CreditorInstitutionAddress address = mockInstance(new CreditorInstitutionAddress());
        details.setAddress(address);
        //when
        CreditorInstitutionDetailsResource resource = mapper.toResource(details);
        //then
        checkNotNullFields(resource, "applicationCode", "auxDigit",
                "mod4", "segregationCode", "broadcast");
        //reflectionEqualsByName(details, resource);
    }

    @Test
    void toCreditorInstitutionDetailsResource_null(){
        //given
        CreditorInstitutionDetails details = null;
        //when
        CreditorInstitutionDetailsResource resource = mapper.toResource(details);
        //then
        assertNull(resource);
    }

    @Test
    void fromCreditorInstitutionDto(){
        //given
        CreditorInstitutionDto dto = mockInstance(new CreditorInstitutionDto());
        CreditorInstitutionAddressDto addressDto = mockInstance(new CreditorInstitutionAddressDto());
        dto.setAddress(addressDto);
        //when
        CreditorInstitutionDetails model = mapper.fromDto(dto);
        //then
        assertNotNull(model);
        checkNotNullFields(model, "applicationCode", "auxDigit",
                "mod4", "segregationCode", "broadcast");
        reflectionEqualsByName(dto, model);
    }

    @Test
    void fromCreditorInstitutionDto_null(){
        //given
        CreditorInstitutionDto dto = null;
        //when
        CreditorInstitutionDetails model = mapper.fromDto(dto);
        //then
        assertNull(model);
    }

    @Test
    void addressToResource(){
        //given
        CreditorInstitutionAddress address = mockInstance(new CreditorInstitutionAddress());
        //when
        CreditorInstitutionAddressResource resource = mapper.toResource(address);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(address, resource);
    }

    @Test
    void addressToResource_null(){
        //given
        CreditorInstitutionAddress address = null;
        //when
        CreditorInstitutionAddressResource resource = mapper.toResource(address);
        //then
        assertNull(resource);
    }

    @Test
    void addressFromDto(){
        //given
        CreditorInstitutionAddressDto dto = mockInstance(new CreditorInstitutionAddressDto());
        //when
        CreditorInstitutionAddress address = mapper.fromDto(dto);
        //then
        assertNotNull(address);
        reflectionEqualsByName(dto, address);
    }

    @Test
    void addressFromDto_null(){
        //given
        CreditorInstitutionAddressDto dto = null;
        //when
        CreditorInstitutionAddress address = mapper.fromDto(dto);
        //then
        assertNull(dto);
    }

    @Test
    void creditorStationEditToResource(){
        //given
        CreditorInstitutionStationEdit model = mockInstance(new CreditorInstitutionStationEdit());
        //when
        CreditorInstitutionStationEditResource resource = mapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void creditorStationEditToResource_null(){
        //given
        CreditorInstitutionStationEdit model = null;
        //when
        CreditorInstitutionStationEditResource resource = mapper.toResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void creditorInstitutinUpdate_fromDto()
    {
        //given
        UpdateCreditorInstitutionDto dto = mockInstance(new UpdateCreditorInstitutionDto());

    }

}