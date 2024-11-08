package com.schoolIntranet;

import com.schoolIntranet.persistence.entity.*;
import com.schoolIntranet.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return args -> {

			// Permissions

			PermissionEntity updateData = PermissionEntity.builder()
					.permissionName(PermissionEnum.UPDATEDATA)
					.build();

			PermissionEntity createAccount = PermissionEntity.builder()
					.permissionName(PermissionEnum.CREATEACCOUNT)
					.build();

			PermissionEntity editAccount = PermissionEntity.builder()
					.permissionName(PermissionEnum.EDITACCOUNT)
					.build();

			PermissionEntity deleteAccount = PermissionEntity.builder()
					.permissionName(PermissionEnum.DELETEACCOUNT)
					.build();

			PermissionEntity unableAccount = PermissionEntity.builder()
					.permissionName(PermissionEnum.UNABLEACCOUNT)
					.build();

			// Roles

			RoleEntity student = RoleEntity.builder()
					.roleName(RoleEnum.STUDENT)
					.permissionEntities(Set.of(updateData))
					.build();

			RoleEntity parent = RoleEntity.builder()
					.roleName(RoleEnum.PARENT)
					.permissionEntities(Set.of(updateData))
					.build();

			RoleEntity teacher = RoleEntity.builder()
					.roleName(RoleEnum.TEACHER)
					.permissionEntities(Set.of(updateData))
					.build();

			RoleEntity admin = RoleEntity.builder()
					.roleName(RoleEnum.ADMIN)
					.permissionEntities(Set.of(updateData, createAccount, editAccount, deleteAccount, unableAccount))
					.build();

			// Test user

			UserEntity adminTest = UserEntity.builder()
					.rut("000000000")
					.name("name0")
					.birthday(LocalDate.of(2024, 10, 27))
					.email("email0@email.com")
					.adress("adress0")
					.phoneNumber("+56912345678")
					.password(new BCryptPasswordEncoder().encode("1234"))
					.accountNonExpired(true)
					.accountNonLocked(true)
					.credentialsNonExpired(true)
					.isEnabled(true)
					.username("adminTest")
					.roleEntities(Set.of(admin))
					.build();

			userRepository.saveAll(List.of(adminTest));
		};
	}
}
