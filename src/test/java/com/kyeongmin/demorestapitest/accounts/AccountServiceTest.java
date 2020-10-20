package com.kyeongmin.demorestapitest.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
//service test하는거니까 controller 상속 받을 필요없어
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRePository accountRePository;

    @Test
    @DisplayName("User를 확인하는 작업")
    public void findByUsername(){
        //Given
        String password = "1234";
        String username="kkm@email.com";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        //account 저장
        this.accountRePository.save(account);
        
        //When
        UserDetailsService userDetailsService = (UserDetailsService)accountService;
        UserDetails userDetails =  userDetailsService.loadUserByUsername(username);

        //Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("user name 불러오려다 실패한 경우")
    public void findByUsernameFail(){
        //메세지 확인하기, 어떤 예외가 발생하길 기대하는지 먼저 적어줘야한다.(미리 적어두기)
        //Expected
        String username = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        //어떤 객체인지 뽑아본다.
        expectedException.expectMessage(Matchers.containsString(username));
        //When
        accountService.loadUserByUsername(username);

        //When
        //assertThrows(UsernameNotFoundException.class, ()->accountService.loadUserByUsername(username));
    }
}
