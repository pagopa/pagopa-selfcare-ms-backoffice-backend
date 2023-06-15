package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.JiraServiceManagerConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JiraServiceManagerServiceImpl implements JiraServiceManagerService {


    private  JiraServiceManagerConnector jiraServiceManagerConnector;

    @Autowired
    public JiraServiceManagerServiceImpl(JiraServiceManagerConnector jiraServiceManagerConnector) {
        this.jiraServiceManagerConnector = jiraServiceManagerConnector;
    }


    @Override
    public String createTicket(String summary, String description) {
        return jiraServiceManagerConnector.createTicket(summary, description);
    }
}
