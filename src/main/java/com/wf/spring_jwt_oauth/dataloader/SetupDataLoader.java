package com.wf.spring_jwt_oauth.dataloader;

import com.wf.spring_jwt_oauth.entities.Role;
import com.wf.spring_jwt_oauth.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component @AllArgsConstructor
public class SetupDataLoader implements ApplicationRunner {


    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {



        Role admin = Role.builder().id(1L).name("ADMIN").build();
        Role user = Role.builder().id(2L).name("USER").build();

        List<Role> roles = new ArrayList<>();
        roles.add(admin);
        roles.add(user);

        roleRepository.saveAll(roles);

    }
}
