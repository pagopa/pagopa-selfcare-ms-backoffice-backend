package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.*;

public class CreditorInstitutionMapperImpl implements CreditorInstitutionMapper {

    @Override
    public CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto) {
        CreditorInstitutionStationEdit station = null;
        if (dto != null) {
            station = new CreditorInstitutionStationEdit();
            station.setStationCode(dto.getStationCode());
            station.setBroadcast(true);
            station.setMod4(true);
//            station.setSegregationCode(1l);//FIXME
            station.setAuxDigit(1l);
//            station.setApplicationCode(1l);
// parlare con Stefano per vedere se vengono compilati da lui
        }
        return station;
    }

    @Override
    public CreditorInstitutionStationEditResource toResource(CreditorInstitutionStationEdit model) {
        CreditorInstitutionStationEditResource resource = null;
        if (model != null) {
            resource = new CreditorInstitutionStationEditResource();
            resource.setStationCode(model.getStationCode());
            resource.setSegregationCode(model.getSegregationCode());
            resource.setMod4(model.getMod4());
            resource.setApplicationCode(model.getApplicationCode());
            resource.setAuxDigit(model.getAuxDigit());
            resource.setBroadcast(model.getBroadcast());
        }
        return resource;
    }

    @Override
    public CreditorInstitutionDetails fromDto(CreditorInstitutionDto dto) {
        if (dto == null) {
            return null;
        }

        CreditorInstitutionDetails creditorInstitutionDetails = new CreditorInstitutionDetails();

        creditorInstitutionDetails.setCreditorInstitutionCode(dto.getCreditorInstitutionCode());
        creditorInstitutionDetails.setEnabled(dto.getEnabled());
        creditorInstitutionDetails.setBusinessName(dto.getBusinessName());
        creditorInstitutionDetails.setAddress(fromDto(dto.getAddress()));
        creditorInstitutionDetails.setPspPayment(dto.getPspPayment());
        creditorInstitutionDetails.setReportingFtp(dto.getReportingFtp());
        creditorInstitutionDetails.setReportingZip(dto.getReportingZip());

        return creditorInstitutionDetails;
    }

    @Override
    public CreditorInstitutionDetailsResource toResource(CreditorInstitutionDetails model) {
        if (model == null) {
            return null;
        }

        CreditorInstitutionDetailsResource creditorInstitutionDetailsResource = new CreditorInstitutionDetailsResource();

        creditorInstitutionDetailsResource.setCreditorInstitutionCode(model.getCreditorInstitutionCode());
        creditorInstitutionDetailsResource.setEnabled(model.getEnabled());
        creditorInstitutionDetailsResource.setBusinessName(model.getBusinessName());
        creditorInstitutionDetailsResource.setAddress(toResource(model.getAddress()));
        creditorInstitutionDetailsResource.setPspPayment(model.getPspPayment());
        creditorInstitutionDetailsResource.setReportingFtp(model.getReportingFtp());
        creditorInstitutionDetailsResource.setReportingZip(model.getReportingZip());

        return creditorInstitutionDetailsResource;
    }

    @Override
    public CreditorInstitutionAddressResource toResource(CreditorInstitutionAddress creditorInstitutionAddress) {
        if (creditorInstitutionAddress == null) {
            return null;
        }

        CreditorInstitutionAddressResource creditorInstitutionAddressResource = new CreditorInstitutionAddressResource();

        creditorInstitutionAddressResource.setLocation(creditorInstitutionAddress.getLocation());
        creditorInstitutionAddressResource.setCity(creditorInstitutionAddress.getCity());
        creditorInstitutionAddressResource.setZipCode(creditorInstitutionAddress.getZipCode());
        creditorInstitutionAddressResource.setCountryCode(creditorInstitutionAddress.getCountryCode());
        creditorInstitutionAddressResource.setTaxDomicile(creditorInstitutionAddress.getTaxDomicile());

        return creditorInstitutionAddressResource;
    }

    @Override
    public CreditorInstitutionAddress fromDto(CreditorInstitutionAddressDto creditorInstitutionAddressDto) {
        if (creditorInstitutionAddressDto == null) {
            return null;
        }

        CreditorInstitutionAddress creditorInstitutionAddress = new CreditorInstitutionAddress();

        creditorInstitutionAddress.setLocation(creditorInstitutionAddressDto.getLocation());
        creditorInstitutionAddress.setCity(creditorInstitutionAddressDto.getCity());
        creditorInstitutionAddress.setZipCode(creditorInstitutionAddressDto.getZipCode());
        creditorInstitutionAddress.setCountryCode(creditorInstitutionAddressDto.getCountryCode());
        creditorInstitutionAddress.setTaxDomicile(creditorInstitutionAddressDto.getTaxDomicile());

        return creditorInstitutionAddress;
    }

    @Override
    public CreditorInstitutionDetails fromDto(UpdateCreditorInstitutionDto dto) {
        CreditorInstitutionDetails creditorInstitutionDetails = null;

        if (dto != null) {
            creditorInstitutionDetails = new CreditorInstitutionDetails();
            creditorInstitutionDetails.setCreditorInstitutionCode(dto.getCreditorInstitutionCode());
            creditorInstitutionDetails.setEnabled(dto.getEnabled());
            creditorInstitutionDetails.setBusinessName(dto.getBusinessName());
            creditorInstitutionDetails.setAddress(fromDto(dto.getAddress()));
            creditorInstitutionDetails.setPspPayment(dto.getPspPayment());
            creditorInstitutionDetails.setReportingFtp(dto.getReportingFtp());
            creditorInstitutionDetails.setReportingZip(dto.getReportingZip());
        }

        return creditorInstitutionDetails;
    }
}
