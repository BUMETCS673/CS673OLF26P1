package edu.bu.metcs673.bluejay.sales.repository;

import edu.bu.metcs673.bluejay.sales.domain.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}