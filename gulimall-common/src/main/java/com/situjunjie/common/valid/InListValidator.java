package com.situjunjie.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class InListValidator implements ConstraintValidator<ValidInList,Integer> {

    Set<Integer> set = new HashSet<>();
    @Override
    public void initialize(ValidInList constraintAnnotation) {
        //ConstraintValidator.super.initialize(constraintAnnotation);
        int[] vals = constraintAnnotation.vals();
        for (int i = 0; i < vals.length; i++) {
            set.add(vals[i]);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
