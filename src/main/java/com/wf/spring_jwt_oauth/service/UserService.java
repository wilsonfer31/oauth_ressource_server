package com.wf.spring_jwt_oauth.service;

import com.wf.spring_jwt_oauth.DTO.RequestLoginDto;
import com.wf.spring_jwt_oauth.entities.Role;
import com.wf.spring_jwt_oauth.entities.User;
import com.wf.spring_jwt_oauth.repository.RoleRepository;
import com.wf.spring_jwt_oauth.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service @AllArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
      return this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User does not exists"));
    }


    public Map<String, String> register(User user){
        Optional<User> u = userRepository.findByEmail(user.getEmail());
        if(u.isPresent()){
            throw new RuntimeException();
        }else {

          List<Role> roleList = user.getRoles().stream().map(role -> roleRepository.findByName(role.getName())).toList();

            var userToSave = User.builder()
                    .email(user.getEmail())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .roles(roleList)
                    .build();

            userRepository.save(userToSave);

            return getStringStringMap(Optional.of(user), "");
        }

    }

    public Map<String, String> login(RequestLoginDto login, String refreshToken){
        Optional<User> userDB= userRepository.findByEmail(login.getEmail());

        if(userDB.isPresent() && passwordEncoder.matches(login.getPassword(), userDB.get().getPassword() )|| refreshToken != null) {
                return getStringStringMap(userDB, refreshToken);
        }else{
            throw new RuntimeException("User dont exists or Wrong password");
        }





}

    private Map<String, String> getStringStringMap(Optional<User> user, String refreshToken) {
        String subject = null;
        String scope = null;

        if (user.isPresent()) {
            subject = user.get().getEmail();
            scope = user.get().getRoles().stream().map(Role::getName).collect(Collectors.joining("-"));

        } else if (refreshToken != null) {
            Jwt decodeToken = null;
            try {
                decodeToken = jwtDecoder.decode(refreshToken);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            subject = decodeToken.getSubject();

            User userLoaded = this.loadUserByUsername(decodeToken.getSubject());
            scope = userLoaded.getRoles().stream().map(Role::getName).collect(Collectors.joining("-"));
        }

        Map<String, String> idToken = new HashMap<>();
        Instant instant = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(refreshToken!=null?1:5, ChronoUnit.MINUTES))
                .issuer("user-service")
                .claim("scope", scope)
                .build();
        String jwtAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken", jwtAccessToken);
        JwtClaimsSet jwtRefreshTokenClaimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(5, ChronoUnit.MINUTES))
                .issuer("user-service")
                .claim("scope", scope)
                .build();
        String jwtRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtRefreshTokenClaimsSet)).getTokenValue();
        idToken.put("refreshToken", jwtRefreshToken);
        return idToken;
    }
}
