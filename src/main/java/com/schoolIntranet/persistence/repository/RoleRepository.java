package com.schoolIntranet.persistence.repository;

import com.schoolIntranet.persistence.entity.RoleEntity;
import com.schoolIntranet.persistence.entity.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    List<RoleEntity> findRolesByRoleNameIn(List<RoleEnum> roleNames);
}
