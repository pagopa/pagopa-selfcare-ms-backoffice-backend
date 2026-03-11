package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperation;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidityDateRequiredForCreateValidator
        implements ConstraintValidator<ValidityDateRequiredForCreate, IbanOperation> {

    @Override
    public boolean isValid(IbanOperation value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isCreate = IbanOperationType.CREATE.equals(value.getType());
        boolean hasValidityDate = value.getValidityDate() != null && !value.getValidityDate().isBlank();

        if (isCreate && !hasValidityDate) {
            buildConstraintViolation(context, "validityDate required for CREATE");
            return false;
        }

        if (!isCreate && hasValidityDate) {
            buildConstraintViolation(context, "validityDate not allow for UPDATE or DELETE");
            return false;
        }

        return true;
    }

    private void buildConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("validityDate")
                .addConstraintViolation();
    }
}