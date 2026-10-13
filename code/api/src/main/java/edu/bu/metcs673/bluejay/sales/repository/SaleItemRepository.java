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

import edu.bu.metcs673.bluejay.sales.domain.SaleItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("SELECT s FROM SaleItem s WHERE s.transactionId = :transactionId")
    List<SaleItem> findByTransactionId(@Param("transactionId") UUID transactionId);

}