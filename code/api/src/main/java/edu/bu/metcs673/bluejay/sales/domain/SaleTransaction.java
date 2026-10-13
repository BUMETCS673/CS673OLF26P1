/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Domain creation and mapping to sale_transaction table
    Human Contributions: None
    Notes: Mapping to the sales_transaction table
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sale_transactions")
@Getter
@Setter
@NoArgsConstructor
public class SaleTransaction {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "cashier_id", length = 36)
    private UUID cashierId;

    @Column(name = "grand_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;
}