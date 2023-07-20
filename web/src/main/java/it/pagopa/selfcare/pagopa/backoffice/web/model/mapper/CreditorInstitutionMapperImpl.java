package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.utils.StringUtils;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CreditorInstitutionMapperImpl implements CreditorInstitutionMapper {

    @Override
    public CreditorInstitutionStationEdit fromDto(CreditorInstitutionStationDto dto) {
        CreditorInstitutionStationEdit station = null;
        if (dto != null) {
            station = new CreditorInstitutionStationEdit();
            station.setStationCode(dto.getStationCode());
            station.setBroadcast(true);
            station.setMod4(true);
            station.setSegregationCode(dto.getSegregationCode());
            station.setAuxDigit(dto.getAuxDigit());
//            station.setApplicationCode(1l);

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
        creditorInstitutionDetails.setBusinessName(StringUtils.truncateString(dto.getBusinessName(),70));
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

    @Override
    public CreditorInstitutionResource toResorce(CreditorInstitution model) {
        if(model == null){
            return null;
        }
        CreditorInstitutionResource creditorInstitutionResource = new CreditorInstitutionResource();

        creditorInstitutionResource.setBusinessName(model.getBusinessName());
        creditorInstitutionResource.setCreditorInstitutionCode(model.getCreditorInstitutionCode());
        creditorInstitutionResource.setEnabled(model.getEnabled());

        return creditorInstitutionResource;
    }

    @Override
    public CreditorInstitutionsResource toResource(CreditorInstitutions model) {
        if(model == null){
            return null;
        }

        CreditorInstitutionsResource creditorInstitutionsResource = new CreditorInstitutionsResource();

        creditorInstitutionsResource.setCreditorInstitutionList(model.getCreditorInstitutionList().stream()
                .map(this::toResorce)
                .collect(Collectors.toList()));
        creditorInstitutionsResource.setPageInfo(model.getPageInfo());

        return creditorInstitutionsResource;
    }

    @Override
    public IbanResource toResource(IbanEnhanced model) {
        if(model == null){
            return null;
        }

        IbanResource resource = new IbanResource();

        resource.setDueDate(model.getDueDate());
        resource.setPublicationDate(model.getPublicationDate());
        resource.setIban(model.getIban());
        resource.setActive(model.isActive());
        resource.setLabels(model.getLabels().stream().map(this::toResource).collect(Collectors.toList()));
        resource.setDescription(model.getDescription());
        resource.setEcOwner(model.getEcOwner());
        resource.setCompanyName(model.getCompanyName());
        resource.setValidityDate(model.getValidityDate());

        return resource;
    }

    @Override
    public IbansResource toResource(IbansEnhanced model) {
        if(model == null){
            return null;
        }

        IbansResource resource = new IbansResource();

        resource.setIbanList(model.getIbanList().stream().map(this::toResource).collect(Collectors.toList()));

        return resource;
    }

    @Override
    public IbanCreate fromDto(IbanCreateRequestDto ibanCreateRequestDto) {
        if(ibanCreateRequestDto == null){
            return null;
        }
        IbanCreate response = new IbanCreate();

        response.setActive(ibanCreateRequestDto.isActive());
        response.setDescription(ibanCreateRequestDto.getDescription());
        response.setIban(ibanCreateRequestDto.getIban());
        if(ibanCreateRequestDto.getLabels() != null)
            response.setLabels(ibanCreateRequestDto.getLabels().stream().map(this::fromDto).collect(Collectors.toList()));
        response.setDueDate(ibanCreateRequestDto.getDueDate());
        response.setValidityDate(ibanCreateRequestDto.getValidityDate());

        return response;
    }

    @Override
    public IbanResource toResource(IbanCreate model) {
        if(model == null){
            return null;
        }

        IbanResource resource = new IbanResource();

        resource.setActive(model.isActive());
        resource.setIban(model.getIban());
        resource.setLabels(model.getLabels().stream().map(this::toResource).collect(Collectors.toList()));
        resource.setDescription(model.getDescription());
        resource.setValidityDate(model.getValidityDate());
        resource.setDueDate(model.getDueDate());

        return resource;

    }

    public IbanCreate toIbanCreate(IbanEnhanced ibanEnhanced){
        if(ibanEnhanced == null){
            return null;
        }

        IbanCreate response = new IbanCreate();
        response.setIban(ibanEnhanced.getIban());
        response.setDescription(ibanEnhanced.getDescription());
        if (ibanEnhanced.getLabels() != null) {
            if (response.getLabels() == null) response.setLabels(new ArrayList<>());
            response.getLabels().addAll(ibanEnhanced.getLabels());
        }
        response.setActive(ibanEnhanced.isActive());
        response.setValidityDate(ibanEnhanced.getValidityDate());
        response.setDueDate(ibanEnhanced.getDueDate());

        return response;
    }

    @Override
    public IbanLabel fromDto(it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel dto) {
        if(dto == null){
            return null;
        }
        IbanLabel response = new IbanLabel();

        response.setDescription(dto.getDescription());
        response.setName(dto.getName());
        return response;
    }

    public it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel toResource(IbanLabel model) {
        if(model == null){
            return null;
        }
        it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel response = new it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbanLabel();

        response.setDescription(model.getDescription());
        response.setName(model.getName());
        return response;
    }


}
