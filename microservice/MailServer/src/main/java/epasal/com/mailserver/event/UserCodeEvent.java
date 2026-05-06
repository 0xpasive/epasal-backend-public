package epasal.com.mailserver.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCodeEvent {
    private String email;
    private String code;
    private Type type;

    public enum Type {
        VERIFICATION,
        PASSWORD_RESET
    }
}
