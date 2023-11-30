package com.example.jdbcexamples;

import com.example.jdbcexamples.dox.GithubUser;
import com.example.jdbcexamples.repository.GithubUserRepository;
import com.example.jdbcexamples.service.GithubUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class GithubUserServiceTest {
    @Autowired
    private GithubUserService githubUserService;
    @Autowired
    private GithubUserRepository githubUserRepository;

    @Test
    public void addGithubUserTest() {
        GithubUser user = GithubUser.builder()
                .name("SUN")
                .followers(15)
                .stars(15)
                .repos(15)
                .gender("female")
                .build();
        githubUserRepository.save(user);
    }

    @Test
    public void a() {
        GithubUser user = GithubUser.builder()
                .gender("male")
                .build();
        githubUserService.listGithubUsers(user).forEach(g -> log.debug(g.getName()));
    }
}
