package com.example.demo;

import com.example.demo.model.Address;
import com.example.demo.model.Gender;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address("England","London","NE28");
			Student student = new Student(
					"John Glenn",
					"Eligio",
					"glenneligio4@gmail.com",
					Gender.MALE,
					address,
					List.of("Math", "Science"),
					BigDecimal.valueOf(200L),
					LocalDateTime.now()
			);

//			usingMongoTemplate(repository, mongoTemplate, student);
			repository.findStudentByEmail(student.getEmail())
					.ifPresentOrElse(s -> {
						System.out.println("Student already exist for the said email");
					}, () -> {
						System.out.println("Student with specified email does not exist");
						System.out.println("Saving student: " + student);
						repository.insert(student);
					});
		};
	}

	private static void usingMongoTemplate(StudentRepository repository, MongoTemplate mongoTemplate, Student student) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is("glenneligio2@gmail.com"));
		List<Student> students = mongoTemplate.find(query, Student.class);

		if(students.size() > 1) {
			throw new IllegalStateException("There can't be multiple Student in single email");
		} else if (students.isEmpty()) {
			System.out.println("Inserting student: " + student);
			repository.insert(student);
		} else {
			System.out.println("Student already exist for the said email");
		}
	}
}
