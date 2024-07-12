package com.example.minorproject1.repositories;

import com.example.minorproject1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
