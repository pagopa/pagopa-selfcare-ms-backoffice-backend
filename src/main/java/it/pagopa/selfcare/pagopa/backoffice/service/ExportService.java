package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.entity.*;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@Slf4j
@Service
public class ExportService {

    @Autowired
    private BrokerIbansRepository brokerIbansRepository;

    @Autowired
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    /**
     * This method is used for exporting a set of IBANs to a CSV format.
     * First, the system gets all the delegations for the input brokerCode.
     * IBAN details are formatted into a CSV row structure.
     *
     * @param brokerCode The broker code used to retrieve delegations and hence the IBANs.
     * @return The byte array representation of the generated CSV file.
     */
    public byte[] exportIbansToCsv(String brokerCode) {
        BrokerIbansEntity ibans = brokerIbansRepository.findByBrokerCode(brokerCode)
                .orElseThrow(() -> new AppException(AppError.BROKER_NOT_FOUND, brokerCode));

        List<String> headers = Arrays.asList("denominazioneEnte", "codiceFiscale", "iban", "stato", "dataAttivazioneIban", "descrizione", "etichetta");
        return Utility.createCsv(headers, mapIbanToCsv(ibans.getIbans()));
    }


    /**
     * This method is used for exporting a set of IBANs to a CSV format.
     * First, the system gets all the delegations for the input brokerCode.
     * IBAN details are formatted into a CSV row structure.
     *
     * @param brokerCode The broker code used to retrieve delegations and hence the IBANs.
     * @return The byte array representation of the generated CSV file.
     */
    public byte[] exportCreditorInstitutionToCsv(String brokerCode) {
        BrokerInstitutionsEntity ibans = brokerInstitutionsRepository.findByBrokerCode(brokerCode)
                .orElseThrow(() -> new AppException(AppError.BROKER_NOT_FOUND, brokerCode));

        List<String> headers = Arrays.asList("companyName", "administrativeCode", "taxCode", "intermediated", "brokerCompanyName",
                "brokerTaxCode", "model", "auxDigit", "segregationCode", "applicationCode", "cbillCode", "stationId", "stationState",
                "activationDate", "version", "broadcast");
        return Utility.createCsv(headers, mapInstitutionToCsv(ibans.getInstitutions()));
    }

    public BrokerECExportStatus getBrokerExportStatus(@NotNull String brokerCode) {
        if (brokerCode == null) {
            throw new AppException(AppError.BAD_REQUEST, "No valid broker code is passed.");
        }
        Optional<ProjectCreatedAt> brokerIbansCreatedAt = Optional.empty();
        Optional<ProjectCreatedAt> brokerInstitutionsCreatedAt = Optional.empty();
        try {
            brokerIbansCreatedAt = brokerIbansRepository.findProjectedByBrokerCode(brokerCode);
            brokerInstitutionsCreatedAt = brokerInstitutionsRepository.findProjectedByBrokerCode(brokerCode);
        } catch (Exception e) {
            log.error(String.format("Error while retrieving export status for broker [%s]", brokerCode), e);
        }
        return BrokerECExportStatus.builder()
                .brokerIbansLastUpdate(brokerIbansCreatedAt.map(ProjectCreatedAt::getCreatedAt).orElse(null))
                .brokerInstitutionsLastUpdate(brokerInstitutionsCreatedAt.map(ProjectCreatedAt::getCreatedAt).orElse(null))
                .build();
    }

    /**
     * This method processes a list of IbanCsv objects, mapping them to a List of lists of String type.
     *
     * @param ibans The list of IbanCsv objects to be processed.
     * @return The list of lists after mapping and ensuring no null values.
     */
    private List<List<String>> mapIbanToCsv(List<BrokerIbanEntity> ibans) {
        return ibans.stream()
                .map(elem -> Arrays.asList(deNull(elem.getCiName()),
                        deNull(elem.getCiFiscalCode()),
                        deNull(elem.getIban()),
                        deNull(elem.getStatus()),
                        deNull(elem.getValidityDate()),
                        deNull(elem.getDescription()),
                        deNull(elem.getLabel())
                ))
                .toList();
    }

    /**
     * This method processes a list of InstitutionCsv objects, mapping them to a List of lists of String type.
     *
     * @param institutions The list of InstitutionCsv objects to be processed.
     * @return The list of lists after mapping and ensuring no null values.
     */
    private List<List<String>> mapInstitutionToCsv(List<BrokerInstitutionEntity> institutions) {
        return institutions.stream()
                .map(elem -> Arrays.asList(
                        deNull(elem.getCompanyName()),
                        deNull(elem.getAdministrativeCode()),
                        deNull(elem.getTaxCode()),
                        Boolean.TRUE.equals(deNull(elem.getIntermediated())) ? "YES" : "NO",
                        deNull(elem.getBrokerCompanyName()),
                        deNull(elem.getBrokerTaxCode()),
                        deNull(elem.getModel()),
                        deNull(elem.getAuxDigit()),
                        deNull(elem.getSegregationCode()),
                        deNull(elem.getApplicationCode()),
                        deNull(elem.getCbillCode()),
                        deNull(elem.getStationId()),
                        deNull(elem.getStationState()),
                        deNull(elem.getActivationDate()),
                        deNull(elem.getVersion()),
                        Boolean.TRUE.equals(deNull(elem.getBroadcast())) ? "ACTIVE" : "INACTIVE"
                ))
                .toList();
    }
}
