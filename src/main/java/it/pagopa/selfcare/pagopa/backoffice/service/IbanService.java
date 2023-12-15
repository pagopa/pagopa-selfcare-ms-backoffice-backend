package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class IbanService {

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    @Autowired
    public IbanService(ApiConfigClient apiConfigClient, ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient, ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
    }


    public Ibans getIban(String ciCode, String labelName) {
        return apiConfigClient.getCreditorInstitutionIbans(ciCode, labelName);
    }

    public Iban createIban(String ciCode, IbanCreate requestDto) {
        IbanCreate dto = apiConfigClient.createCreditorInstitutionIbans(ciCode, requestDto);
        return modelMapper.map(dto, Iban.class);
    }

    public Iban updateIban(String ciCode, String ibanValue, IbanCreate dto) {
        // updating labels, owned by other CI, related to the passed IBAN
        if(!isEmpty(dto.getLabels())) {
            Ibans ibansEnhanced = apiConfigClient.getCreditorInstitutionIbans(ciCode, dto.getLabels().get(0).getName());
            if(ibansEnhanced != null && !ObjectUtils.isEmpty(ibansEnhanced.getIbanList())) {
                ibansEnhanced.getIbanList().forEach(iban -> {
                    IbanCreate ibanCreate = modelMapper.map(iban, IbanCreate.class);
                    List<IbanLabel> ibanLabelList = ibanCreate.getLabels().stream().filter(f -> !(f.getName().equals(dto.getLabels().get(0).getName()))).collect(Collectors.toList());
                    ibanCreate.setLabels(ibanLabelList);
                    apiConfigClient.updateCreditorInstitutionIbans(ciCode, iban.getIban(), ibanCreate);
                });
            }
        }
        // update IBAN values
        IbanCreate updatedDto = apiConfigClient.updateCreditorInstitutionIbans(ciCode, ibanValue, dto);
        return modelMapper.map(updatedDto, Iban.class);
    }

    public void deleteIban(String ciCode, String ibanValue) {
        apiConfigClient.deleteCreditorInstitutionIbans(ciCode, ibanValue);
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
        var delegations = externalApiClient.getBrokerDelegation(null, brokerCode, "prod-pagopa", "FULL");
        List<String> taxCodes = delegations.stream()
                .map(DelegationExternal::getTaxCode)
                .collect(Collectors.toList());

        List<IbanCsv> ibans = retrieveIbans(taxCodes);
        List<String> headers = Arrays.asList("denominazioneEnte", "codiceFiscale", "iban", "stato", "dataAttivazioneIban", "descrizione", "etichetta");
        return Utility.createCsv(headers, mapToCsv(ibans));
    }

    /**
     * The retrieveIbans method retrieves the IBANs for a given list of taxCodes.
     * <p>
     * This method creates a list of CompletableFuture tasks. Each task processes a partition of the original taxCodes list,
     * with each partition not exceeding a predetermined limit (100). The partition is necessary as the list of taxCodes can contain
     * more than one thousand items.
     * <p>
     * It awaits the completion of all the tasks, collects the results from all the CompletableFuture
     * tasks into a List, and returns this list.
     * <p>
     * If any task completes exceptionally, an empty ArrayList is returned.
     *
     * @param taxCodes List of tax codes for which the IBANs are to be retrieved
     * @return List of IbanCsv containing the retrieved IBANs for the given tax codes
     */
    private List<IbanCsv> retrieveIbans(List<String> taxCodes) {
        List<CompletableFuture<List<IbanCsv>>> futures = new ArrayList<>();
        int limit = 100;
        for (int i = 0; i < taxCodes.size(); i += limit) {
            // we divide the taxCodes in partitions (the list can have a size > 1000)
            List<String> partition = taxCodes.subList(i, Math.min(i + limit, taxCodes.size()));

            // foreach partition we create parallel requests
            CompletableFuture<List<IbanCsv>> future = CompletableFuture.supplyAsync(() -> {
                int numberOfPages = getNumberOfPages(partition, limit);

                // we iterate all the pages and then transforming and collecting them into a list of "IbanCsv" objects.
                return IntStream.rangeClosed(0, numberOfPages)
                        .parallel()
                        .mapToObj(j -> apiConfigSelfcareIntegrationClient.getIbans(limit, j, partition))
                        .flatMap(elem -> elem.getIbans().stream())
                        .map(IbanService::mapToCsvRow)
                        .collect(Collectors.toList());
            });
            futures.add(future);
        }
        var allFuturesResult = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allFuturesResult
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .join();
    }

    /**
     * This is a private method that determines the number of pages required to display a partitioned list of tax codes.
     *
     * @param partition a subset list of tax codes
     * @param limit     the maximum number of tax codes to be displayed on each page
     * @return an integer representing the number of pages needed to display all tax codes in the partition
     */

    private int getNumberOfPages(List<String> partition, int limit) {
        var pageInfo = apiConfigSelfcareIntegrationClient.getIbans(1, 0, partition);
        return (int) Math.floor((double) pageInfo.getPageInfo().getTotalItems() / limit);
    }

    /**
     * This method processes a list of IbanCsv objects, mapping them to a List of lists of String type.
     *
     * @param ibans The list of IbanCsv objects to be processed.
     * @return The list of lists after mapping and ensuring no null values.
     */
    private List<List<String>> mapToCsv(List<IbanCsv> ibans) {
        return ibans.stream()
                .map(elem -> Arrays.asList(deNull(elem.getDenominazioneEnte()),
                        deNull(elem.getCodiceFiscale()),
                        deNull(elem.getIban()),
                        deNull(elem.getStato()),
                        deNull(elem.getDataAttivazioneIban()),
                        deNull(elem.getDescrizione()),
                        deNull(elem.getEtichetta())
                ))
                .collect(Collectors.toList());
    }

    private static String isEnabled(IbanDetails elem) {
        return OffsetDateTime.now().isBefore(elem.getDueDate()) ? "ATTIVO" : "DISATTIVO";
    }

    /**
     * The mapToCsvRow method takes an instance of IbanDetails as input, maps its properties to an instance of IbanCsv and returns it.
     *
     * @param elem an instance of IbanDetails
     * @return an instance of IbanCsv with its attributes mapped from the provided IbanDetails
     */
    private static IbanCsv mapToCsvRow(IbanDetails elem) {
        return IbanCsv.builder()
                .denominazioneEnte(elem.getCiName())
                .codiceFiscale(elem.getCiFiscalCode())
                .stato(isEnabled(elem))
                .dataScadenza(String.valueOf(elem.getDueDate()))
                .dataAttivazioneIban(String.valueOf(elem.getValidityDate()))
                .descrizione(elem.getDescription())
                .iban(elem.getIban())
                .etichetta(elem.getLabels().stream()
                        .map(IbanLabel::getName)
                        .collect(Collectors.joining(" - ")))
                .build();
    }
}
