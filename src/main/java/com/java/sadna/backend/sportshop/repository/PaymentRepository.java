package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(Long orderId);

    // Refund inside the cancel transaction. status='SUCCESS' gate prevents double-refund.
    @Modifying
    @Query(value = """
            UPDATE payments
               SET status                = 'REFUNDED',
                   refund_transaction_id = :refundTransactionId,
                   updated_at            = NOW(),
                   updated_by            = :userId
             WHERE order_id = :orderId
               AND status   = 'SUCCESS'
            """, nativeQuery = true)
    int refundIfSuccess(@Param("orderId") Long orderId,
                        @Param("userId") Long userId,
                        @Param("refundTransactionId") String refundTransactionId);
}
