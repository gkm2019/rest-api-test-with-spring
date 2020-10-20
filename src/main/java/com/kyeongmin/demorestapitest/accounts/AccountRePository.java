package com.kyeongmin.demorestapitest.accounts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRePository extends JpaRepository<Account, Integer> {
    //null을 return 할 수 있으니 optional로 감싸준다.
    Optional<Account> findByEmail(String username);
}
