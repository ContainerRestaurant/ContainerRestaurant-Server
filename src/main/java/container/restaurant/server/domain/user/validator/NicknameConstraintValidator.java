package container.restaurant.server.domain.user.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicknameConstraintValidator implements ConstraintValidator<NicknameConstraint, String> {

    private final static int MIN_LENGTH = 2;
    private final static int MAX_LENGTH = 20;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        int length = 0;

        if (!value.matches("[a-zA-z0-9가-힣]+"))
            return false;

        Pattern alnum = Pattern.compile("[a-zA-z0-9]");
        Matcher alnumMatch = alnum.matcher(value);
        while (alnumMatch.find()) {
            if (++length > MAX_LENGTH)
                return false;
        }

        Pattern korean = Pattern.compile("[가-힣]");
        Matcher koreanMatch = korean.matcher(value);
        while (koreanMatch.find()) {
            if ((length += 2) > MAX_LENGTH)
                return false;
        }

        return length >= MIN_LENGTH;
    }

}
