package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий закупок
 */
@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, UUID> {



}
