package com.eachserver.security.authority;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository
        extends CrudRepository<Authority, String>, PagingAndSortingRepository<Authority, String> {

    @Query(
            "SELECT a.\"id\", a.\"version\", a.\"name\", a.\"updated_at\", a.\"updated_by\" FROM"
                + " \"security_authority\" a INNER JOIN \"security_role_authority\" ra ON a.\"id\""
                + " = ra.\"authority_id\" INNER JOIN \"security_role\" r ON ra.\"role_id\" ="
                + " r.\"id\" INNER JOIN \"security_user_role\" ur ON ur.\"role_id\" = r.\"id\""
                + " WHERE ur.\"user_id\" = :userId")
    Collection<Authority> findByUserId(final String userId);

    Page<Authority> findById(String id, Pageable pageable);

    Page<Authority> findByName(String name, Pageable pageable);
}
