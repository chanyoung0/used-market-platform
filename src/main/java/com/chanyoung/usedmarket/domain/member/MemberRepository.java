package com.chanyoung.usedmarket.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);


}
