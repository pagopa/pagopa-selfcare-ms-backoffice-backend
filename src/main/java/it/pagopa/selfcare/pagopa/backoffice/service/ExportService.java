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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@Slf4j
@Service
public class ExportService {

    private static final List<String> BUNDLE_EXPORT_HEADERS = Arrays.asList("Id", "Nome", "Descrizione", "Tipologia", "Commissione", "Importo pagamento minimo", "Importo pagamento massimo", "Tipo di pagamento", "Touchpoint", "Valido da", "Valido a", "Ultima Modifica", "Id canale", "Nome PSP", "Codice fiscale PSP", "Gestione carrello di pagamenti", "Pagamento con marca da bollo", "Pagamento solo con marca da bollo");

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
     * @param pspCode        the PSP code used to retrieve the bundles
     * @param bundleTypeList the list of bundle type to be retrieved
     * @return The byte array representation of the generated CSV file.
     */
    public byte[] exportPSPBundlesToCsv(String pspCode, List<BundleType> bundleTypeList) {
        Set<Bundle> pspBundles = this.bundleAllPages.getAllPSPBundles(pspCode, bundleTypeList);

        return Utility.createCsv(BUNDLE_EXPORT_HEADERS, mapBundlesToStringList(pspBundles));
    }

    private List<List<String>> mapBundlesToStringList(Set<Bundle> pspBundles) {
        return pspBundles.parallelStream()
                .map(bundle -> Arrays.asList(
                        deNull(bundle.getId()),
                        deNull(bundle.getName()),
                        deNull(bundle.getDescription()),
                        mapBundleType(bundle.getType()),
                        formatCurrency(bundle.getPaymentAmount()),
                        formatCurrency(bundle.getMinPaymentAmount()),
                        formatCurrency(bundle.getMaxPaymentAmount()),
                        mapTouchpointAndPaymentType(bundle.getPaymentType()),
                        mapTouchpointAndPaymentType(bundle.getTouchpoint()),
                        deNull(bundle.getValidityDateFrom()),
                        deNull(bundle.getValidityDateTo()),
                        deNull(bundle.getLastUpdatedDate()),
                        deNull(bundle.getIdChannel()),
                        deNull(bundle.getPspBusinessName()),
                        deNull(bundle.getIdBrokerPsp()),
                        parseBoolean(bundle.getCart()),
                        parseBoolean(bundle.getDigitalStamp()),
                        parseBoolean(bundle.getDigitalStampRestriction())
                ))
                .toList();
    }

    private String mapTouchpointAndPaymentType(String value) {
        value = deNull(value);
        if (value.isBlank() || value.equals("ANY")) {
            return "Tutti";
        }
        return value;
    }

    private String parseBoolean(Boolean booleanValue) {
        return Boolean.TRUE.equals(deNull(booleanValue)) ? "true" : "false";
    }

    private String formatCurrency(Long value) {
        if (value == null) {
            return "";
        }
        BigDecimal valueToFormat = BigDecimal.valueOf(value / 100.00);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ITALY);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(valueToFormat);
    }

    private String mapBundleType(BundleType bundleType) {
        if (BundleType.GLOBAL.equals(bundleType)) {
            return "Per tutti";
        }
        if (BundleType.PUBLIC.equals(bundleType)) {
            return "Su richiesta";
        }
        if (BundleType.PRIVATE.equals(bundleType)) {
            return "Su invito";
        }
        return "";
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
                        parseBoolean(elem.getIntermediated()),
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
                        parseBoolean(elem.getBroadcast()),
                        parseBoolean(elem.getPspPayment()),
                        deNull(elem.getEndpointRT()),
                        deNull(elem.getEndpointRedirect()),
                        deNull(elem.getEndpointMU()),
                        deNull(elem.getPrimitiveVersion()),
                        parseBoolean(elem.getCiStatus())))
                .toList();
    }
}
