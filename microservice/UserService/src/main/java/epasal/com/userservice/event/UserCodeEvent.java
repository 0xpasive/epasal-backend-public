package epasal.com.userservice.event;

import epasal.com.userservice.entity.Codes;
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
    private Codes.Type type;
}
