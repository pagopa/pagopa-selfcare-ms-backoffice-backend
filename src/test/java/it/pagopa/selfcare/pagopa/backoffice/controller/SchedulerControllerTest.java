package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.scheduler.CiBrokerExtractionScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "cron.job.manual-trigger.enabled=true")
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class SchedulerControllerTest {

    @MockBean
    private CiBrokerExtractionScheduler ciBrokerExtractionScheduler;

    @Autowired
    private MockMvc mvc;

    @Test
    void triggerCiBrokerExtraction() throws Exception {
        mvc.perform(post("/jobs/ci-broker-extraction"))
                .andExpect(status().isAccepted());

        verify(ciBrokerExtractionScheduler).extractCI();
    }
}
