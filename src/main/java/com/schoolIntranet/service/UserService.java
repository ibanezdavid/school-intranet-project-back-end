package com.schoolIntranet.service;

import com.schoolIntranet.persistence.entity.UserEntity;
import com.schoolIntranet.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userEntity.getRoleEntities() != null) {
            userEntity.getRoleEntities().clear();
        }
        userRepository.delete(userEntity);
    }

    public void unableAccount(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userEntity.setEnabled(false);
        userRepository.save(userEntity);
    }

    public void updateUserData(UserEntity userEntityUpdated, String username) {
        UserEntity userEntityAuth = findByUsername(username);
        userEntityAuth.setName(userEntityUpdated.getName());
        userEntityAuth.setBirthday(userEntityUpdated.getBirthday());
        userEntityAuth.setEmail(userEntityUpdated.getEmail());
        userEntityAuth.setAdress(userEntityUpdated.getAdress());
        userEntityAuth.setPhoneNumber(userEntityUpdated.getPhoneNumber());
        userEntityAuth.setUsername(userEntityUpdated.getUsername());
        saveUser(userEntityAuth);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public void editAccount(Long id, UserEntity userEntityUpdated) {
        UserEntity userEntityActual = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userEntityActual.setName(userEntityUpdated.getName());
        userEntityActual.setBirthday(userEntityUpdated.getBirthday());
        userEntityActual.setEmail(userEntityUpdated.getEmail());
        userEntityActual.setAdress(userEntityUpdated.getAdress());
        userEntityActual.setPhoneNumber(userEntityUpdated.getPhoneNumber());
        userEntityActual.setUsername(userEntityUpdated.getUsername());

        if (userEntityUpdated.getPassword() != null && !userEntityUpdated.getPassword().isEmpty()) {
            userEntityActual.setPassword(encodePassword(userEntityUpdated.getPassword()));
        }

        userEntityActual.setRut(userEntityUpdated.getRut());
        userRepository.save(userEntityActual);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
