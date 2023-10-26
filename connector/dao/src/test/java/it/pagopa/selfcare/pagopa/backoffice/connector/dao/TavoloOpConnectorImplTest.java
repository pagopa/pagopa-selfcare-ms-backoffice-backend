package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.auditing.SpringSecurityAuditorAware;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TavoloOpConnectorImplTest {

    private final static String LOGGED_USER_ID = "id";

    private final SelfCareUser selfCareUser;
    private final TavoloOpRepository repositoryMock;
    private final MongoTemplate mongoTemplateMock;
    private final TavoloOpConnectorImpl tavoloOpConnector;


    public TavoloOpConnectorImplTest() {
        selfCareUser = SelfCareUser.builder(LOGGED_USER_ID).build();
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(selfCareUser, null);
        TestSecurityContextHolder.setAuthentication(authenticationToken);
        this.repositoryMock = mock(TavoloOpRepository.class);
        this.mongoTemplateMock = mock(MongoTemplate.class);
        this.tavoloOpConnector = new TavoloOpConnectorImpl(new SpringSecurityAuditorAware(), repositoryMock);
    }


    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        reset(repositoryMock, mongoTemplateMock);
    }

    @Test
    void insert_tavoloOp_duplicateKey() {
        // given

        TavoloOp dto = new TavoloOp();
        dto.setReferent("setReferent");
        dto.setEmail("setEmail");
        dto.setName("setName");
        dto.setTaxCode("setTaxCode");
        dto.setCreatedBy("id");


        TavoloOpEntity entity = new TavoloOpEntity();
        entity.setReferent("setReferent");
        entity.setEmail("setEmail");
        entity.setName("setName");
        entity.setTaxCode("setTaxCode");
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setModifiedAt(dto.getModifiedAt());
        entity.setModifiedBy("id");
        entity.setId("setTaxCode");

        Optional<TavoloOpEntity> opt = Optional.of(entity);


        doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(any(TavoloOpEntity.class));


        when(repositoryMock
                .insert(any(TavoloOpEntity.class))).thenReturn(entity);
        // when
        TavoloOpEntity saved = (TavoloOpEntity) tavoloOpConnector.insert(dto);
        // then
        assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_tavoloOp() {
        // given
        TavoloOp tavoloOp = new TavoloOp();
        tavoloOp.setReferent("setReferent");
        tavoloOp.setEmail("setEmail");
        tavoloOp.setName("setName");
        tavoloOp.setTaxCode("setTaxCode");
        tavoloOp.setCreatedBy("id");
        tavoloOp.setId("setTaxCode");


        TavoloOpEntity entity = new TavoloOpEntity();
        entity.setReferent("setReferent");
        entity.setEmail("setEmail");
        entity.setName("setName");
        entity.setTaxCode("setTaxCode");
        entity.setCreatedBy(tavoloOp.getCreatedBy());
        entity.setModifiedAt(tavoloOp.getModifiedAt());
        entity.setId("setTaxCode");

        when(repositoryMock
                .insert(any(TavoloOpEntity.class))).thenReturn(entity);

        // when
        TavoloOpEntity saved = (TavoloOpEntity) tavoloOpConnector.insert(tavoloOp);
        // then
        assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void get_tavoloOp() {
        // given
        String taxCode = "taxCode";

        TavoloOpEntity entity = new TavoloOpEntity();
        entity.setReferent("setReferent");
        entity.setEmail("setEmail");
        entity.setName("setName");
        entity.setTaxCode("TaxCode");

        when(repositoryMock
                .findByTaxCode(anyString())).thenReturn(entity);

        // when
        TavoloOpEntity saved = (TavoloOpEntity) tavoloOpConnector.findByTaxCode(taxCode);
        // then
        assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .findByTaxCode(taxCode);
        verifyNoMoreInteractions(repositoryMock);
    }


}
