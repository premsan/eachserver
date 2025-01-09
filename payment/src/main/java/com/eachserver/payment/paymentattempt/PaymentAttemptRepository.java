package com.eachserver.payment.paymentattempt;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAttemptRepository extends CrudRepository<PaymentAttempt, String> {}
