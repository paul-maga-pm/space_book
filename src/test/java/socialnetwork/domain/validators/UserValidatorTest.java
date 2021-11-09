package socialnetwork.domain.validators;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.models.User;
import socialnetwork.exceptions.InvalidEntityException;

class UserValidatorTest {
    UserValidator validationStrategy = new UserValidator();
    String invalidIdMessage = "Id can't be negative.\n";
    String invalidFirstNameMessage = "First name can't be empty.\n";
    String invalidLastNameMessage = "Last name can't be empty.\n";
    String allFieldsAreInvalidMessage = invalidIdMessage
            .concat(invalidFirstNameMessage)
            .concat(invalidLastNameMessage);
    @Test
    void shouldNotThrowInvalidEntityException(){
        User validUser = new User(100L, "John", "Snow");
        validationStrategy.validate(validUser);
    }

    @Test
    void allFieldsAreInvalid(){
        User userWithAllFieldsInvalid = new User(-100L, "", "");

        Assertions.assertThrows(InvalidEntityException.class, () -> {
            validationStrategy.validate(userWithAllFieldsInvalid);
        });
    }

    @Test
    void idIsInvalid(){
        User userWithInvalidId = new User(-100L, "Snow", "Snow");
        Assertions.assertThrows(InvalidEntityException.class,
                () -> {validationStrategy.validate(userWithInvalidId);},
                invalidIdMessage);
    }

    @Test
    void firstNameIsInvalid(){
        User userWithInvalidFirstName = new User(100L, "", "Snow");
        Assertions.assertThrows(InvalidEntityException.class,
                () -> {validationStrategy.validate(userWithInvalidFirstName);},
                invalidFirstNameMessage);
    }

    @Test
    void lastNameIsInvalid(){
        User userWithInvalidLastName = new User(100L, "Snow", "");
        Assertions.assertThrows(InvalidEntityException.class,
                () -> {validationStrategy.validate(userWithInvalidLastName);},
                invalidLastNameMessage);
    }
}