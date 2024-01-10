package it.pagopa.selfcare.pagopa.backoffice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OpenApiTableMetadata {

    boolean internal() default true;

    boolean external() default false;

    boolean synchronous() default true;

    String authorization() default "JWT";

    String authentication() default "JWT";

    float tps() default 1;

    boolean idempotency() default true;

    boolean stateless() default true;

    ReadWrite readWriteIntense() default ReadWrite.NONE;

    boolean cacheable() default false;

    @Getter
    @AllArgsConstructor
    enum ReadWrite {
        NONE(""),
        READ("Read"),
        WRITE("Write"),
        BOTH("Read and Write");

        public final String value;
    }
}
