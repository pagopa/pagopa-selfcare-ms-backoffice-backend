package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.entity.*;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@Slf4j
@Service
public class ExportService {

    private final BrokerIbansRepository brokerIbansRepository;

    private final BrokerInstitutionsRepository brokerInstitutionsRepository;

    private final BundleAllPages bundleAllPages;

    @Autowired
    public ExportService(
            BrokerIbansRepository brokerIbansRepository,
            BrokerInstitutionsRepository brokerInstitutionsRepository,
            BundleAllPages bundleAllPages
    ) {
        this.brokerIbansRepository = brokerIbansRepository;
        this.brokerInstitutionsRepository = brokerInstitutionsRepository;
        this.bundleAllPages = bundleAllPages;
    }

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

        List<String> headers = Arrays.asList("companyName", "taxCode", "intermediated", "brokerCompanyName",
                "brokerTaxCode", "model", "auxDigit", "segregationCode", "applicationCode", "cbillCode", "stationId",
                "stationState", "activationDate", "version", "broadcast", "PSPpayment", "endpointRT", "endpointRedirect",
                "endpointMU", "primitiveVersion", "ciStatus");
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
     * This method is used for exporting all bundle of the specified PSP and of the specified types.
     * Retrieve all the bundles and then format the list in a CSV row structure.
     *
     * @param pspCode the PSP code used to retrieve the bundles
     * @param bundleTypeList the list of bundle type to be retrieved
     * @return The byte array representation of the generated CSV file.
     */
    public byte[] exportPSPBundlesToCsv(String pspCode, List<BundleType> bundleTypeList) {
        Set<Bundle> pspBundles = this.bundleAllPages.getAllPSPBundles(pspCode, bundleTypeList);

        List<String> headers = Arrays.asList(
                "id",
                "nome",
                "descrizione",
                "commissione",
                "importo pagamento minimo",
                "importo pagamento massimo",
                "touchpoint",
                "tipo",
                "valido da",
                "valido a",
                "id canale",
                "carrello"
        );
        return Utility.createCsv(headers, pspBundles.parallelStream()
                .map(bundle -> Arrays.asList(
                        deNull(bundle.getId()),
                        deNull(bundle.getName()),
                        deNull(bundle.getDescription()),
                        deNull(bundle.getPaymentAmount()),
                        deNull(bundle.getMinPaymentAmount()),
                        deNull(bundle.getMaxPaymentAmount()),
                        deNull(bundle.getTouchpoint()),
                        deNull(bundle.getType().name()),
                        deNull(bundle.getValidityDateFrom()),
                        deNull(bundle.getValidityDateTo()),
                        deNull(bundle.getIdChannel()),
                        Boolean.TRUE.equals(deNull(bundle.getCart())) ? "true" : "false")
                )
                .toList());
    }

    /**
     * This method processes a list of IbanCsv objects, mapping them to a List of lists of String type.
     *
     * @param ibans The list of IbanCsv objects to be processed.
     * @return The list of lists after mapping and ensuring no null values.
     */
    private List<List<String>> mapIbanToCsv(List<IbanEntity> ibans) {
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
                        deNull(elem.getTaxCode()),
                        Boolean.TRUE.equals(deNull(elem.getIntermediated())) ? "true" : "false",
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
                        Boolean.TRUE.equals(deNull(elem.getBroadcast())) ? "true" : "false",
                        Boolean.TRUE.equals(deNull(elem.getPspPayment())) ? "true" : "false",
                        deNull(elem.getEndpointRT()),
                        deNull(elem.getEndpointRedirect()),
                        deNull(elem.getEndpointMU()),
                        deNull(elem.getPrimitiveVersion()),
                        Boolean.TRUE.equals(deNull(elem.getCiStatus())) ? "true" : "false"))
                .toList();
    }
}
