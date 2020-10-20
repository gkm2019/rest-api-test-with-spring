package com.kyeongmin.demorestapitest.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service //bean 등록
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRePository accountRePository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //우리가 사용하는 Account도메인을 srping security가 정의해놓은 UserDetail로 변환하는 작업
        Account account = accountRePository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException(username)); //data 비어있으면 error 던진다.(username찾을 수 없다는 예외처리)
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        //role -> GrantedAuthority로 변환
        return roles.stream()
                .map(r->new SimpleGrantedAuthority("ROLE"+r.name()))
                .collect(Collectors.toSet());
    }
}
