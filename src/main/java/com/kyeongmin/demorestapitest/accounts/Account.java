package com.kyeongmin.demorestapitest.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Integer id;
    private String email;
    private String password;
    
    @ElementCollection(fetch=FetchType.EAGER) //가져올때마다 매칭해야하니까 eager모드
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
