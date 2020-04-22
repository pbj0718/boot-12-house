package com.etoak.utils;


import com.etoak.exception.ParamException;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

public class ValidationUtil {
    private static Validator validator;

    static{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static void validate(Object object){
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if(CollectionUtils.isNotEmpty(violations)){
            Iterator<ConstraintViolation<Object>> iterator = violations.iterator();
            StringBuffer messageBuffer = new StringBuffer();
            while(iterator.hasNext()){
                ConstraintViolation<Object> violation = iterator.next();
                String message = violation.getMessage();
                messageBuffer.append(message).append(";");
            }
            throw new ParamException("参数异常: " + messageBuffer.toString());
        }
    }

}
