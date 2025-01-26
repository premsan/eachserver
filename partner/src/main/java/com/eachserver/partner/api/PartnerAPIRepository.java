package com.eachserver.partner.api;

import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerAPIRepository extends ListCrudRepository<PartnerAPI, String> {

    List<PartnerAPI> findByPath(final String path);
}
