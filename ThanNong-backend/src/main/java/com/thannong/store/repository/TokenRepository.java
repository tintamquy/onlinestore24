package com.thannong.store.repository;

import com.thannong.store.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository truy vấn bảng t_token. */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
