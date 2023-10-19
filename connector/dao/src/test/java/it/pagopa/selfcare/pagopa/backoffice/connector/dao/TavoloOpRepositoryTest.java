package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.config.DaoTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {TavoloOp.class, TavoloOpRepository.class, DaoTestConfig.class})
class TavoloOpRepositoryTest {

    @Autowired
    private TavoloOpRepository repository;


    @AfterEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void insert() {
        // given
        TavoloOp entity = mockInstance(new TavoloOp());
        // when
        TavoloOp saved = repository.insert(entity);
        // then
        assertEquals(entity, saved);
    }


}