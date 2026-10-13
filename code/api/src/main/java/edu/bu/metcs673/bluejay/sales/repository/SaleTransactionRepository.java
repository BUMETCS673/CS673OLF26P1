/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Interface creation
    Human Contributions: None
    Notes:
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.repository;

import edu.bu.metcs673.bluejay.sales.domain.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, UUID> {
}