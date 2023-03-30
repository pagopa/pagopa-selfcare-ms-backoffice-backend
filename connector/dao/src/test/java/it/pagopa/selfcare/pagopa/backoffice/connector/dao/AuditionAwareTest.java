package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.auditing.SpringSecurityAuditorAware;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditionAwareTest {


     @Test
     public void testGetCurrentAuditor_Authentication() {
         // given
         SecurityContext securityContext = mock(SecurityContext.class);
         Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");
         when(securityContext.getAuthentication()).thenReturn(authentication);
         SecurityContextHolder.setContext(securityContext);

         // when
         SpringSecurityAuditorAware auditorAware = new SpringSecurityAuditorAware();

         // then
         Optional<String> result = auditorAware.getCurrentAuditor();
         assertTrue(result.isPresent());
         assertEquals("user", result.get());
     }

     @Test
     public void testGetCurrentAuditor_noAuthentication() {
         // given
         SecurityContextHolder.clearContext();

         // when
         SpringSecurityAuditorAware auditorAware = new SpringSecurityAuditorAware();

         // then
         Optional<String> result = auditorAware.getCurrentAuditor();
         assertFalse(result.isPresent());
     }
}
