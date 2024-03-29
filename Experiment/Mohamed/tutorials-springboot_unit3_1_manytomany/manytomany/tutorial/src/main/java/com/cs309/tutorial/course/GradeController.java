package com.cs309.tutorial.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/grade")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepo;

    @PostMapping("/create")
    public void createGrade(@RequestParam Long studentId, @RequestParam Long courseId, @RequestParam Double value) {
        Grade grade = new Grade();
        // Assuming we have methods to fetch Student and Course by ID
        // grade.setStudent(studentRepo.findById(studentId).orElse(null));
        // grade.setCourse(courseRepo.findById(courseId).orElse(null));
        grade.setValue(value);
        gradeRepo.save(grade);
    }

    @GetMapping("/get")
    public Grade getGrade(@RequestParam Long id) {
        return gradeRepo.findById(id).orElse(null);
    }
}
