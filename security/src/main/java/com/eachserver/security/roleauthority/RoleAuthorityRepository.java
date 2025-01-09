package com.eachserver.security.roleauthority;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAuthorityRepository extends CrudRepository<RoleAuthority, String> {}
