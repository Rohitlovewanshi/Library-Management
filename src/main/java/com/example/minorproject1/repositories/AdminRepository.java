package com.example.minorproject1.repositories;

import com.example.minorproject1.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
