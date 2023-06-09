package org.catenax.atp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueMatcherValidator implements ConstraintValidator<ValueMatcher,String> {

    private String expectedValue;

    @Override
    public boolean isValid(final String value, ConstraintValidatorContext context) {
        return value.equals(this.expectedValue);
    }

    @Override
    public void initialize(final ValueMatcher constraintAnnotation) {
        expectedValue = constraintAnnotation.expectedValue();
    }
}
