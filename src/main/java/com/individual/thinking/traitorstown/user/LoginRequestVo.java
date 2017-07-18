package com.individual.thinking.traitorstown.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class LoginRequestVo {
    private String email;
    private String password;
}
