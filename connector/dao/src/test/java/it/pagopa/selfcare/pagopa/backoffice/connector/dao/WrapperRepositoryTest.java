package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.config.DaoTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DataMongoTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {WrapperEntities.class, WrapperRepository.class, DaoTestConfig.class})
class WrapperRepositoryTest {

    @Autowired
    private WrapperRepository repository;


    @AfterEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void insert() {
        // given
        ChannelDetails entity = mockInstance(new ChannelDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        // when
        WrapperEntities saved = repository.insert(wrapperEntities);
        // then
        assertEquals(wrapperEntities, saved);
    }

    @Test
    void findById() {
        // given
        String code = "code";
        ChannelDetails entity = mockInstance(new ChannelDetails());
        entity.setChannelCode("code");
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        repository.insert(wrapperEntities);
        // when
        Optional  <WrapperEntities> responseOpt = repository.findById(code);
        // then
        assertTrue(responseOpt.isPresent());
    }

    @Test
    void update() {
        // given
        String code = "code";
        ChannelDetails entity = mockInstance(new ChannelDetails());
        ChannelDetails entityNew = mockInstance(new ChannelDetails());
        entity.setChannelCode("code");
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        repository.insert(wrapperEntities);

        wrapperEntities.getEntities().add(new WrapperEntity<>(entityNew));

        // when
        WrapperEntities<Object> response =   repository.save(wrapperEntities);
        // then
        assertNotNull(response);
    }
}