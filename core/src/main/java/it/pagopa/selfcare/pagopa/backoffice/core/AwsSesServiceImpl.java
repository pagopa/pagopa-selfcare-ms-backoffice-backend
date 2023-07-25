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
    public String sendEmail(String to, String subject, String body) {
        log.trace("sendEmail start");
        log.debug("sendEmail , to = {}, subject = {}, body = {}", to, subject, body);
        String result = null;
        try {
            result = awsSesConnector.sendEmail(to, subject, body);
        }catch (Exception e){
            log.error(e.getMessage());
            result = "sendEmail error to = "+to+", subject = "+subject;
        }
        log.debug("sendEmail result = {}", result);
        log.trace("sendEmail end");
        return result;
    }
}
