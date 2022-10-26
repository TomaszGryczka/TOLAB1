package com.github.tomaszgryczka.testowanie;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    private String login;
    private String firstName;
    private String lastName;
    private String email;
}
