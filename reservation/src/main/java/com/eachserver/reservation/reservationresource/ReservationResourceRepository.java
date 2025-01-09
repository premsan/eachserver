package com.eachserver.reservation.reservationresource;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationResourceRepository
        extends CrudRepository<ReservationResource, String> {}
