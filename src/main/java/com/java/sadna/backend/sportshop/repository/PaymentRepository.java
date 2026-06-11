package com.java.sadna.backend.sportshop.repository;

import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
