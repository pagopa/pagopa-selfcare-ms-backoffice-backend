package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import it.pagopa.selfcare.pagopa.backoffice.repository.TransactionalBulkDAO;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class IbanByBrokerExtractionSchedulerTest {

    private static final int PAGE_LIMIT = 5;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    @MockBean
    private TransactionalBulkDAO dao;

    @Autowired
    @InjectMocks
    private IbanByBrokerExtractionScheduler scheduler;


    @ParameterizedTest
    @CsvSource({
            "7,8,6,true",
            "0,8,6,true",
            "7,0,6,true",
            "7,8,0,true",
            "7,8,6,false"
    })
    void extract_ok(Integer totalBrokers, int totalCIsPerBroker, int totalIbansPerCI, String excludePagoPA) throws IOException {

        // setting runtime variables
        int startId = 2;
        int endId = 4;
        Set<String> brokerCodesToBeExcluded = getBrokerCodesToBeExcluded(startId, endId);

        // mocking injected parameters
        boolean excludePagoPABroker = Boolean.parseBoolean(excludePagoPA);
        setParameterValues(excludePagoPABroker);

        // mocking DAO methods
        doNothing().when(dao).init();
        when(dao.getAllBrokerCodeGreaterThan(any(Date.class))).thenReturn(brokerCodesToBeExcluded);
        doNothing().when(dao).save(any(BrokerIbansEntity.class));
        doNothing().when(dao).clean(any(Date.class));

        // mocking API client responses
        Set<String> brokerECMockMerged = mockGetBrokersEC(totalBrokers);
        Set<String> brokerAnalyzed = new HashSet<>(Set.copyOf(brokerECMockMerged));
        brokerAnalyzed.removeAll(brokerCodesToBeExcluded);
        if(excludePagoPABroker) {
            brokerAnalyzed.remove(Constants.PAGOPA_BROKER_CODE);
        }
        mockGetIbans(totalCIsPerBroker, totalIbansPerCI, brokerECMockMerged, false);

        // executing main logic
        scheduler.extract();

        // execute assertions
        verify(dao, times(brokerAnalyzed.size())).save(any(BrokerIbansEntity.class));
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    void extract_ko(String excludePagoPA) throws IOException {

        // setting runtime variables
        int totalBrokers = 7;
        int totalCIsPerBroker = 8;

        // mocking injected parameters
        boolean excludePagoPABroker = Boolean.parseBoolean(excludePagoPA);
        setParameterValues(excludePagoPABroker);

        // mocking DAO methods
        doNothing().when(dao).init();
        when(dao.getAllBrokerCodeGreaterThan(any(Date.class))).thenReturn(new HashSet<>());

        // mocking API client responses
        Set<String> brokerECMockMerged = mockGetBrokersEC(totalBrokers);
        mockGetIbans(totalCIsPerBroker, PAGE_LIMIT, brokerECMockMerged, true);

        // executing main logic
        scheduler.extract();

        // execute assertions
        verify(dao, times(0)).save(any(BrokerIbansEntity.class));
    }

    private Set<String> mockGetBrokersEC(int totalBrokers) {
        int pages = (int) Math.floor((double) totalBrokers / IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT) + 1;
        List<Broker> brokerECMockMergedPages = new ArrayList<>();
        when(apiConfigClient.getBrokersEC(1, 0, null, null, null, null)).thenReturn(getBrokerECPageMock(totalBrokers));
        for (int page = 0; page < pages; page++) {
            Brokers brokerECMockPage = getBrokerECMock(0, totalBrokers);
            when(apiConfigClient.getBrokersEC(IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, page, null, null, null, null)).thenReturn(brokerECMockPage);
            brokerECMockMergedPages.addAll(brokerECMockPage.getBrokerList());
        }
        return brokerECMockMergedPages.stream().map(Broker::getBrokerCode).collect(Collectors.toSet());
    }

    private void mockGetIbans(int totalCIsPerBroker, int totalIbansPerCI, Set<String> brokerECMockMerged, boolean throwInError) {
        for (String brokerECIdMock : brokerECMockMerged) {
            CreditorInstitutionsView getCIsByBrokerMockPage0 = getCIsByBrokerMock(0, IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, totalCIsPerBroker, brokerECIdMock);
            CreditorInstitutionsView getCIsByBrokerMockPage1 = getCIsByBrokerMock(1, IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, totalCIsPerBroker, brokerECIdMock);

            when(apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(1, 0, null, brokerECIdMock, null, true, null, null, null, null)).thenReturn(getCIsByBrokerPageMock(totalCIsPerBroker));
            when(apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(5, 0, null, brokerECIdMock, null, true, null, null, null, null)).thenReturn(getCIsByBrokerMockPage0);
            when(apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(5, 1, null, brokerECIdMock, null, true, null, null, null, null)).thenReturn(getCIsByBrokerMockPage1);

            List<CreditorInstitutionView> cisByBrokerMockMergedPages = new ArrayList<>();
            cisByBrokerMockMergedPages.addAll(getCIsByBrokerMockPage0.getCreditorInstitutionList());
            cisByBrokerMockMergedPages.addAll(getCIsByBrokerMockPage1.getCreditorInstitutionList());
            Set<String> partition = cisByBrokerMockMergedPages.stream().map(CreditorInstitutionView::getIdDominio).collect(Collectors.toSet());
            List<String> partitionAsList = new ArrayList<>(partition);

            if(throwInError) {
                when(apiConfigSCIntClient.getIbans(1, 0, partitionAsList)).thenReturn(null);
            } else if(totalCIsPerBroker > 0) {
                IbansList ibanListMockPage0 = getIbansMock(0, IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, totalIbansPerCI, partitionAsList);
                IbansList ibanListMockPage1 = getIbansMock(1, IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, totalIbansPerCI, partitionAsList);
                when(apiConfigSCIntClient.getIbans(1, 0, partitionAsList)).thenReturn(getIbansPageMock(totalIbansPerCI));
                when(apiConfigSCIntClient.getIbans(5, 0, partitionAsList)).thenReturn(ibanListMockPage0);
                when(apiConfigSCIntClient.getIbans(5, 1, partitionAsList)).thenReturn(ibanListMockPage1);
            }
        }
    }


    private void setParameterValues(boolean avoidExportPagoPABroker) {
        ReflectionTestUtils.setField(scheduler, "getBrokersPageLimit", IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT);
        ReflectionTestUtils.setField(scheduler, "getIbansPageLimit", IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT);
        ReflectionTestUtils.setField(scheduler, "getCIByBrokerPageLimit", IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT);
        ReflectionTestUtils.setField(scheduler, "avoidExportPagoPABroker", avoidExportPagoPABroker);
    }

    private static Brokers getBrokerECPageMock(long totalItems) {
        return Brokers.builder()
                .pageInfo(getPageMock(totalItems))
                .build();
    }

    private static CreditorInstitutionsView getCIsByBrokerPageMock(long totalItems) {
        return CreditorInstitutionsView.builder()
                .pageInfo(getPageMock(totalItems))
                .build();
    }

    private static IbansList getIbansPageMock(long totalItems) {
        return IbansList.builder()
                .pageInfo(getPageMock(totalItems))
                .build();
    }

    private static PageInfo getPageMock(long totalItems) {
        return PageInfo.builder()
                .page(0)
                .limit(1)
                .totalPages((int) totalItems)
                .itemsFound(1)
                .totalItems(totalItems)
                .build();
    }

    private static Brokers getBrokerECMock(int page, long totalItems) {
        int lowerLimit = IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT * page + 1;
        int upperLimit = (int) Math.min(((long) IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT * page) + IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT, totalItems);

        Brokers result = Brokers.builder()
                .brokerList(IntStream.rangeClosed(lowerLimit, upperLimit)
                        .mapToObj(id -> Broker.builder()
                                .brokerCode(id == 1 ? Constants.PAGOPA_BROKER_CODE : "BRO" + id)
                                .enabled(true)
                                .description("Mock broker with id " + id)
                                .brokerDetails("Some detail")
                                .build())
                        .toList())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT)
                        .totalPages((int) Math.floor((double) totalItems / IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT) + 1)
                        .itemsFound(IbanByBrokerExtractionSchedulerTest.PAGE_LIMIT)
                        .totalItems(totalItems)
                        .build())
                .build();
        result.getPageInfo().setItemsFound(result.getBrokerList().size());
        return result;
    }

    private static CreditorInstitutionsView getCIsByBrokerMock(int page, int limit, long totalItems, String brokerCode) {
        int lowerLimit = limit * page + 1;
        int upperLimit = (int) Math.min(((long) limit * page) + limit, totalItems);

        List<CreditorInstitutionView> list = IntStream.rangeClosed(lowerLimit, upperLimit)
                .mapToObj(id -> CreditorInstitutionView.builder()
                        .idDominio("CI" + id + brokerCode)
                        .idIntermediarioPa(brokerCode)
                        .idStazione(brokerCode + "_" + id)
                        .build())
                .collect(Collectors.toList());
        CreditorInstitutionsView result = CreditorInstitutionsView.builder()
                .creditorInstitutionList(list)
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(limit)
                        .totalPages((int) Math.floor((double) totalItems / limit) + 1)
                        .itemsFound(limit)
                        .totalItems(totalItems)
                        .build())
                .build();
        result.getPageInfo().setItemsFound(result.getCreditorInstitutionList().size());
        return result;
    }

    private static IbansList getIbansMock(int page, int limit, long totalItems, List<String> partition) {
        int lowerLimit = limit * page + 1;
        int upperLimit = (int) Math.min(((long) limit * page) + limit, totalItems);

        IbansList result = IbansList.builder()
                .ibans(IntStream.rangeClosed(lowerLimit, upperLimit)
                        .mapToObj(id -> IbanDetails.builder()
                                .ciName(partition.get(id))
                                .ciFiscalCode(partition.get(id))
                                .iban("IT00X" + id + partition.get(id))
                                .insertedDate(OffsetDateTime.now())
                                .validityDate(OffsetDateTime.now())
                                .dueDate(OffsetDateTime.now())
                                .description("iban description")
                                .ownerFiscalCode("owner-fiscal-code")
                                .labels(List.of())
                                .build())
                        .toList())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(limit)
                        .totalPages((int) Math.floor((double) totalItems / limit) + 1)
                        .itemsFound(limit)
                        .totalItems(totalItems)
                        .build())
                .build();
        result.getPageInfo().setItemsFound(result.getIbans().size());
        return result;
    }


    private static Set<String> getBrokerCodesToBeExcluded(int idStart, int idEnd) {
        return IntStream.rangeClosed(idStart, idEnd)
                .mapToObj(id -> "BRO" + id)
                .collect(Collectors.toSet());
    }
}
