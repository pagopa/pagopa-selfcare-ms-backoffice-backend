package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ConvertInstitutionInfoToInstitutionResource.class)

class ConvertInstitutionInfoToInstitutionResourceTest {

    @Autowired
    private ConvertInstitutionInfoToInstitutionResource sut;

    @Test
    void convertSuccess() {
        Billing billingObj = new Billing();
        billingObj.setRecipientCode("recipientCode");

        BusinessData businessData = new BusinessData();
        businessData.setRea("rea");
        businessData.setShareCapital("shareCapital");
        businessData.setBusinessRegisterPlace("businessRegisterPlace");

        SupportContact supportContact = new SupportContact();
        supportContact.setSupportEmail("supportEmail");
        supportContact.setSupportPhone("supportPhone");

        PaymentServiceProvider psp = new PaymentServiceProvider();
        psp.setBusinessRegisterNumber("businessRegisterNumber");
        psp.setLegalRegisterName("legalRegisterName");
        psp.setLegalRegisterNumber("legalRegisterNumber");
        psp.setAbiCode("abiCode");
        psp.setVatNumberGroup(true);

        DataProtectionOfficer dpo = new DataProtectionOfficer();
        dpo.setAddress("dpoAddress");
        dpo.setPec("dpoPec");
        dpo.setEmail("dpoEmail");

        InstitutionInfo institutionInfo = InstitutionInfo.builder()
                .id("ID")
                .institutionType(InstitutionType.PA)
                .description("description")
                .digitalAddress("digitalAddress")
                .externalId("externalId")
                .origin("origin")
                .originId("originId")
                .taxCode("taxCode")
                        .userProductRoles(Collections.singletonList("productRole"))
                                .billing(billingObj)
                .businessData(businessData)
                .supportContact(supportContact)
                .paymentServiceProvider(psp)
                .dataProtectionOfficer(dpo)
                .address("address")
                .status("status")
                .build();

        MappingContext<InstitutionInfo,InstitutionDetail> context = Mockito.mock(MappingContext.class);
        when(context.getSource()).thenReturn(institutionInfo);
        AtomicReference<InstitutionDetail> conversion = new AtomicReference<>();
        assertDoesNotThrow(() ->
                conversion.set(sut.convert(context)));

        InstitutionDetail atomicContent = conversion.get();

        assertEquals(institutionInfo.getId(), atomicContent.getId());
        assertEquals(institutionInfo.getDescription(), atomicContent.getDescription());
        assertEquals(institutionInfo.getInstitutionType(), atomicContent.getInstitutionType());
        assertEquals(institutionInfo.getDigitalAddress(), atomicContent.getDigitalAddress());
        assertEquals(institutionInfo.getExternalId(), atomicContent.getExternalId());
        assertEquals(institutionInfo.getOrigin(), atomicContent.getOrigin());
        assertEquals(institutionInfo.getOriginId(), atomicContent.getOriginId());
        assertEquals(institutionInfo.getTaxCode(), atomicContent.getTaxCode());

        assertEquals(institutionInfo.getUserProductRoles().get(0), atomicContent.getUserProductRoles().get(0).getProductRole());

        assertEquals(institutionInfo.getBilling().getRecipientCode(), atomicContent.getRecipientCode());

        CompanyInformation companyInformation = atomicContent.getCompanyInformations();
        assertEquals(businessData.getRea(), companyInformation.getRea());
        assertEquals(businessData.getShareCapital(), companyInformation.getShareCapital());
        assertEquals(businessData.getBusinessRegisterPlace(), companyInformation.getBusinessRegisterPlace());

        AssistanceContact assistanceContact = atomicContent.getAssistanceContacts();
        assertEquals(supportContact.getSupportEmail(), assistanceContact.getSupportEmail());
        assertEquals(supportContact.getSupportPhone(), assistanceContact.getSupportPhone());

        PspData pspData = atomicContent.getPspData();
        assertEquals(psp.getBusinessRegisterNumber(), pspData.getBusinessRegisterNumber());
        assertEquals(psp.getLegalRegisterName(), pspData.getLegalRegisterName());
        assertEquals(psp.getLegalRegisterNumber(), pspData.getLegalRegisterNumber());
        assertEquals(psp.getAbiCode(), pspData.getAbiCode());
        assertEquals(psp.getVatNumberGroup(), pspData.getVatNumberGroup());

        DpoData dpoData = atomicContent.getDpoData();
        assertEquals(dpo.getAddress(), dpoData.getAddress());
        assertEquals(dpo.getPec(), dpoData.getPec());
        assertEquals(dpo.getEmail(), dpoData.getEmail());

        assertEquals(institutionInfo.getAddress(), atomicContent.getAddress());
        assertEquals(institutionInfo.getStatus(), atomicContent.getStatus());
    }

}