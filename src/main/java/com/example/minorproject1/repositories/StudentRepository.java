package com.example.minorproject1.repositories;

import com.example.minorproject1.model.Student;
import com.example.minorproject1.model.StudentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Transactional
    @Modifying
    @Query("update Student s set s.status = ?2 where s.id = ?1")
    void deactivate(int studentId, StudentStatus status);
}
