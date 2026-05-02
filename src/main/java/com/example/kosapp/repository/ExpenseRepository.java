package com.example.kosapp.repository;

import com.example.kosapp.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByBoardingHouseId(Long boardingHouseId);
}
