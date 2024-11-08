package com.schoolIntranet.service;

import com.schoolIntranet.controller.dto.AuthCreateRoleRequest;
import com.schoolIntranet.controller.dto.AuthCreateUserRequest;
import com.schoolIntranet.controller.dto.AuthLoginRequest;
import com.schoolIntranet.controller.dto.AuthResponse;
import com.schoolIntranet.persistence.entity.RoleEntity;
import com.schoolIntranet.persistence.entity.RoleEnum;
import com.schoolIntranet.persistence.entity.UserEntity;
import com.schoolIntranet.persistence.repository.RoleRepository;
import com.schoolIntranet.persistence.repository.UserRepository;
import com.schoolIntranet.util.JwtUtils;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoleEntities()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleName().name()))));

        userEntity.getRoleEntities().stream()
                .flatMap(role -> role.getPermissionEntities().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName().name())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();
        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(username, "user logged successfully", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest) {
        String rut = authCreateUserRequest.getRut();
        String name = authCreateUserRequest.getName();
        LocalDate birthday = authCreateUserRequest.getBirthday();
        String email = authCreateUserRequest.getEmail();
        String adress = authCreateUserRequest.getAdress();
        String phoneNumber = authCreateUserRequest.getPhoneNumber();
        String password = authCreateUserRequest.getPassword();
        String username = authCreateUserRequest.getUsername();
        List<String> roleRequest = authCreateUserRequest.getRoleRequest().roleListName();

        List<RoleEnum> roleEnums = roleRequest.stream()
                .map(RoleEnum::valueOf)
                .collect(Collectors.toList());

        Set<RoleEntity> roleEntitySet = roleRepository.findRolesByRoleNameIn(roleEnums).stream().collect(Collectors.toSet());
        if(roleEntitySet.isEmpty()){
            throw new IllegalIdentifierException("The specified role does not exist");
        }

        UserEntity userEntity = UserEntity.builder()
                .rut(rut)
                .name(name)
                .birthday(birthday)
                .email(email)
                .adress(adress)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(password))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .username(username)
                .roleEntities(roleEntitySet)
                .build();

        UserEntity userCreated = userRepository.save(userEntity);
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userCreated.getRoleEntities().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleName().name()))));
        userCreated.getRoleEntities()
                .stream()
                .flatMap(role -> role.getPermissionEntities().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName().name())));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);
        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "user created successfully", accessToken, true);
        return authResponse;
    }

    public void importUsersFromCsv(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            CsvToBean<AuthCreateUserRequest> csvToBean = new CsvToBeanBuilder<AuthCreateUserRequest>(reader)
                    .withType(AuthCreateUserRequest.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<AuthCreateUserRequest> users = csvToBean.parse();
            for (AuthCreateUserRequest userRequest : users) {
                List<String> roleList = userRequest.getRoleRequest().roleListName();
                AuthCreateRoleRequest roleRequest = createRoleRequest(roleList);
                AuthCreateUserRequest newUserRequest = new AuthCreateUserRequest(
                        userRequest.getRut(),
                        userRequest.getName(),
                        userRequest.getBirthday(),
                        userRequest.getEmail(),
                        userRequest.getAdress(),
                        userRequest.getPhoneNumber(),
                        userRequest.getPassword(),
                        userRequest.getUsername(),
                        roleRequest
                );
                createUser(newUserRequest);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error importing users", e);
        }
    }

    private AuthCreateRoleRequest createRoleRequest(List<String> roleList) {
        if (roleList == null || roleList.isEmpty()) {
            throw new IllegalArgumentException("Role list cannot be null or empty");
        }
        for (String role : roleList) {
            try {
                RoleEnum.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
        }
        return new AuthCreateRoleRequest(roleList);
    }
}
