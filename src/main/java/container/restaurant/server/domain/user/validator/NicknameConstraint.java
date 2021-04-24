package container.restaurant.server.domain.user.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NicknameConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NicknameConstraint {
    String message() default
            "닉네임은 한글/영문/숫자/공백만 입력 가능하며, 1~10자의 한글이나 2~20자의 영문/숫자/공백만 입력 가능합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
