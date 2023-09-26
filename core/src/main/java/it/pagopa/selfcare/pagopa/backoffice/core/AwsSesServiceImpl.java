package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.AwsSesConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AwsSesServiceImpl implements AwsSesService {

    private final AwsSesConnector awsSesConnector;

    @Autowired
    public AwsSesServiceImpl(AwsSesConnector awsSesConnector) {
        this.awsSesConnector = awsSesConnector;
    }

    @Override
    public String sendEmail( String subject, String body,String...to) {
        String result = null;
        try {
            result = awsSesConnector.sendEmail(subject, body,to);
        }catch (Exception e){
            result = "sendEmail error to = "+to+", subject = "+subject;
        }
        return result;
    }
}
