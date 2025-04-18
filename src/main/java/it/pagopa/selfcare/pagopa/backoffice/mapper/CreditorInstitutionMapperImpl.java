package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.util.StringUtils;

public class CreditorInstitutionMapperImpl implements CreditorInstitutionMapper {

    @Override
    public BrokerDetails fromDto(BrokerEcDto dto) {
        BrokerDetails brokerDetails = null;
        if(dto != null) {
            brokerDetails = new BrokerDetails();
            brokerDetails.setExtendedFaultBean(dto.getExtendedFaultBean());
            brokerDetails.setDescription(dto.getDescription());
            brokerDetails.setEnabled(dto.getEnabled());
            brokerDetails.setBrokerCode(dto.getBrokerCode());
        }
        return brokerDetails;
    }

    @Override
    public CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto) {
        CreditorInstitutionStationEdit station = null;
        if(dto != null) {
            station = new CreditorInstitutionStationEdit();
            station.setStationCode(dto.getStationCode());
            station.setMod4(dto.getMod4());
            station.setSegregationCode(dto.getSegregationCode());
            station.setAuxDigit(dto.getAuxDigit());
            station.setBroadcast(dto.getBroadcast());
            station.setApplicationCode(dto.getApplicationCode());
            station.setAca(dto.getAca());
            station.setStandIn(dto.getStandIn());
        }
        return station;
    }

    @Override
    public CreditorInstitutionStationEditResource toResource(CreditorInstitutionStationEdit model) {
        CreditorInstitutionStationEditResource resource = null;
        if(model != null) {
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
        if(dto == null) {
            return null;
        }

        CreditorInstitutionDetails creditorInstitutionDetails = new CreditorInstitutionDetails();

        creditorInstitutionDetails.setCreditorInstitutionCode(dto.getCreditorInstitutionCode());
        creditorInstitutionDetails.setEnabled(dto.getEnabled());
        creditorInstitutionDetails.setBusinessName(StringUtils.truncateString(dto.getBusinessName(), 70));
        creditorInstitutionDetails.setCbillCode(dto.getCbillCode());
        creditorInstitutionDetails.setAddress(fromDto(dto.getAddress()));
        creditorInstitutionDetails.setPspPayment(dto.getPspPayment());
        creditorInstitutionDetails.setReportingFtp(dto.getReportingFtp());
        creditorInstitutionDetails.setReportingZip(dto.getReportingZip());

        return creditorInstitutionDetails;
    }

    @Override
    public BrokerDetailsResource toResource(BrokerDetails model) {
        if(model == null) {
            return null;
        }
        BrokerDetailsResource brokerDetailsResource = new BrokerDetailsResource();
        brokerDetailsResource.setBrokerCode(model.getBrokerCode());
        brokerDetailsResource.setEnabled(model.getEnabled());
        brokerDetailsResource.setExtendedFaultBean(model.getExtendedFaultBean());
        brokerDetailsResource.setDescription(model.getDescription());

        return brokerDetailsResource;
    }

    @Override
    public CreditorInstitutionDetailsResource toResource(CreditorInstitutionDetails model) {
        if(model == null) {
            return null;
        }

        CreditorInstitutionDetailsResource creditorInstitutionDetailsResource = new CreditorInstitutionDetailsResource();

        creditorInstitutionDetailsResource.setCiTaxCode(model.getCreditorInstitutionCode());
        creditorInstitutionDetailsResource.setEnabled(model.getEnabled());
        creditorInstitutionDetailsResource.setBusinessName(model.getBusinessName());
        creditorInstitutionDetailsResource.setCbillCode(model.getCbillCode());
        creditorInstitutionDetailsResource.setAddress(toResource(model.getAddress()));
        creditorInstitutionDetailsResource.setPspPayment(model.getPspPayment());
        creditorInstitutionDetailsResource.setReportingFtp(model.getReportingFtp());
        creditorInstitutionDetailsResource.setReportingZip(model.getReportingZip());

        return creditorInstitutionDetailsResource;
    }

    @Override
    public CreditorInstitutionAddressResource toResource(CreditorInstitutionAddress creditorInstitutionAddress) {
        if(creditorInstitutionAddress == null) {
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
        if(creditorInstitutionAddressDto == null) {
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

        if(dto != null) {
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

    @Override
    public CreditorInstitutionResource toResorce(CreditorInstitution model) {
        if(model == null) {
            return null;
        }
        CreditorInstitutionResource creditorInstitutionResource = new CreditorInstitutionResource();

        creditorInstitutionResource.setBusinessName(model.getBusinessName());
        creditorInstitutionResource.setCiTaxCode(model.getCreditorInstitutionCode());
        creditorInstitutionResource.setCbillCode(model.getCbillCode());
        creditorInstitutionResource.setEnabled(model.getEnabled());
        creditorInstitutionResource.setBroadcast(model.getBroadcast());
        creditorInstitutionResource.setMod4(model.getMod4() == null ? null : model.getMod4().toString());
        creditorInstitutionResource.setApplicationCode(longToString(model.getApplicationCode()));
        creditorInstitutionResource.setAuxDigit(longToString(model.getAuxDigit()));
        creditorInstitutionResource.setSegregationCode(longToString(model.getSegregationCode()));
        creditorInstitutionResource.setAca(model.getAca());
        creditorInstitutionResource.setStandIn(model.getStandIn());

        return creditorInstitutionResource;
    }

    @Override
    public CreditorInstitutionsResource toResource(CreditorInstitutions model) {
        if(model == null) {
            return null;
        }

        CreditorInstitutionsResource creditorInstitutionsResource = new CreditorInstitutionsResource();

        creditorInstitutionsResource.setCreditorInstitutionList(model.getCreditorInstitutionList().stream()
                .map(this::toResorce)
                .toList());
        creditorInstitutionsResource.setPageInfo(model.getPageInfo());

        return creditorInstitutionsResource;
    }


    private String longToString(Long value) {
        return value == null ? null : value.toString();
    }
}
