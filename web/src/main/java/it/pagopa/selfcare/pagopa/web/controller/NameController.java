package it.pagopa.selfcare.pagopa.web.controller;

import io.swagger.annotations.Api;
import it.pagopa.selfcare.pagopa.core.NameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@Api(tags = "product")
public class NameController {//TODO change Name

    private final NameService nameService;//TODO change Name


    @Autowired
    public NameController(NameService nameService) {
        this.nameService = nameService;
    }

}
