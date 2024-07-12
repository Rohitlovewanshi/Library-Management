package com.example.minorproject1.controller;

import com.example.minorproject1.dtos.CreateStudentRequest;
import com.example.minorproject1.dtos.GetStudentDetailsResponse;
import com.example.minorproject1.dtos.UpdateStudentRequest;
import com.example.minorproject1.model.Authority;
import com.example.minorproject1.model.Student;
import com.example.minorproject1.model.User;
import com.example.minorproject1.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping() //
    public Integer createStudent(@Valid @RequestBody CreateStudentRequest request){
        return studentService.create(request);
    }

    @GetMapping("/admin/{studentId}")
    public GetStudentDetailsResponse getStudentDetailsForAdmin(@PathVariable("studentId") Integer studentId) throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = (User)authentication.getPrincipal();

//        if (!user.getAuthorities().contains(Authority.ADMIN)){
//            throw new Exception("user is not an admin");
//        }

//        if (user.getAdmin() == null || user.getStudent() != null){
//            throw new Exception("user is not an admin");
//        }

        return this.studentService.getStudentDetails(studentId);
    }

    @GetMapping("")
    public GetStudentDetailsResponse getStudentDetails() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = (User)authentication.getPrincipal();
        Student student = user.getStudent();

        Integer studentId = null;
        if (student!= null){
            studentId = student.getId();
        } else {
            throw new Exception("User is not student");
        }
        return this.studentService.getStudentDetails(studentId);
    }

    @PatchMapping("")
    public GetStudentDetailsResponse updateStudentDetails(
            @Valid @RequestBody UpdateStudentRequest request) throws Exception {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = (User)authentication.getPrincipal();
        Student student = user.getStudent();

        Integer studentId = null;
        if (student!= null){
            studentId = student.getId();
        } else {
            throw new Exception("User is not student");
        }

        return this.studentService.update(studentId,request);
    }

    @DeleteMapping("")
    public GetStudentDetailsResponse deactivateStudent() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = (User)authentication.getPrincipal();
        Student student = user.getStudent();

        Integer studentId = null;
        if (student!= null){
            studentId = student.getId();
        } else {
            throw new Exception("User is not student");
        }

        return studentService.deactivate(studentId);
    }

}
