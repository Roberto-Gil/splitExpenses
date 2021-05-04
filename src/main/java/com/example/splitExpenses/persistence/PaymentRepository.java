package com.example.splitExpenses.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("select sum(p.amount) from Payment p")
    double sumTotalPayments();

}
