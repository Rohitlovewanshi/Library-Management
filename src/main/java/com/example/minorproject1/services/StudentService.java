package com.example.minorproject1.services;

import com.example.minorproject1.dtos.CreateStudentRequest;
import com.example.minorproject1.dtos.GetStudentDetailsResponse;
import com.example.minorproject1.dtos.UpdateStudentRequest;
import com.example.minorproject1.model.*;
import com.example.minorproject1.repositories.StudentCacheRepository;
import com.example.minorproject1.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentCacheRepository studentCacheRepository;

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

    public GetStudentDetailsResponse getStudentDetails(Integer studentId){

        Student student = this.studentCacheRepository.get(studentId);

        if (student != null){
            return GetStudentDetailsResponse.builder()
                    .student(student)
                    .bookList(student.getBookList())
                    .build();
        }

        student = this.studentRepository.findById(studentId).orElse(null);

//        List<Book> bookList = this.bookService.getBooksByStudentId(studentId);

        this.studentCacheRepository.add(student);

        return GetStudentDetailsResponse.builder()
                .student(student)
                .bookList(student.getBookList())
                .build();
    }

    public Integer create(CreateStudentRequest request) {

        Student student = request.mapToStudent();
        User user = this.userService.create(student.getUser(), Authority.STUDENT);
        student.setUser(user);
        this.studentRepository.save(student);
        return student.getId();
    }

    public GetStudentDetailsResponse update(Integer studentId,UpdateStudentRequest request) {
        Student student = request.mapToStudent();
        GetStudentDetailsResponse studentDetailsResponse = this.getStudentDetails(studentId);

        Student savedStudent = studentDetailsResponse.getStudent();

//        if (request.getName()!=null) {
//            savedStudent.setName(request.getName());
//        }
//        if (request.getEmail()!=null) {
//            savedStudent.setEmail(request.getEmail());
//        }
//        if (request.getMobile()!=null) {
//            savedStudent.setMobile(request.getMobile());
//        }

        Student target = merge(student,savedStudent);

        studentRepository.save(target);
        studentDetailsResponse.setStudent(target);
        return studentDetailsResponse;
    }

    private Student merge(Student incoming, Student saved){
        JSONObject incomingStudent = mapper.convertValue(incoming, JSONObject.class);
        JSONObject savedStudent = mapper.convertValue(saved, JSONObject.class);

        JSONObject[] objs = new JSONObject[] {savedStudent, incomingStudent};
        for (JSONObject obj : objs){
            Iterator it = obj.keySet().iterator();;
            while(it.hasNext()){
                String key = (String)it.next();
                if(obj.get(key)!=null){
                    savedStudent.put(key,obj.get(key));
                }
            }
        }
        return mapper.convertValue(savedStudent, Student.class);
    }

    public GetStudentDetailsResponse deactivate(int studentId) {
        this.studentRepository.deactivate(studentId, StudentStatus.INACTIVE);
        return this.getStudentDetails(studentId);
    }
}
