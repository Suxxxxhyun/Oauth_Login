package com.psc.sample.domain;

import com.psc.sample.dto.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
//구글, 네이버로 로그인시 이메일정보를 가져오고,
//사용자는 이후에 아이디와 이름을 추가로 기입할 수 있다.
public class UserInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //구글,네이버로 로그인시 이메일정보를 가져올 수 있게 함.
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}


