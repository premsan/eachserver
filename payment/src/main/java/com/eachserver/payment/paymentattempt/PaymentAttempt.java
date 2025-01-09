package com.eachserver.payment.paymentattempt;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "payment_payment_attempt")
public class PaymentAttempt {

    @Id
    @Column("id")
    private String id;

    @Version
    @Column("version")
    private Long version;

    @Column("payment_id")
    private String paymentId;

    @Column("gateway_id")
    private String gatewayId;

    @Column("gateway_attempt_id")
    private String gatewayAttemptId;

    @Column("gateway_attempt_url")
    private String gatewayAttemptUrl;

    @Column("gateway_attempt_attributes")
    private Map<String, String> gatewayAttemptAttributes;

    @Column("status")
    private PaymentAttemptStatus status;

    @Column("updated_at")
    private Long updatedAt;

    @Column("updated_by")
    private String updatedBy;
}
