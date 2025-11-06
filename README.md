# spring-jpa-courseenrollments
This project explores a Spring Data JPA based student enrollments in courses having many-to-many relationship between the entities Student and Course.


## Usage

1. Build
```
    .\gradlew build
```    

2. Test
```
    .\gradlew test
```

3. Run the app
```
    .\gradlew bootRun
```

4. Add a course
```
curl -X POST "http://localhost:8080/api/courses" -H "Content-Type: application/json" -d "{\"courseName\":\"Data Structures\",\"courseCode\":\"CS201\",\"credits\":4}"
```

5. Add a Student
```
curl -X POST "http://localhost:8080/api/students" -H "Content-Type: application/json" -d "{\"firstName\":\"Foo\",\"lastName\":\"Bar\",\"email\":\"jane.smith@example.com\",\"enrollmentDate\":\"2024-01-15\"}"
```

6. Add an enrollement
```
curl -X POST "http://localhost:8080/api/enrollments" -H "Content-Type: application/json" -d "{\"studentId\":1,\"courseId\":1,\"enrollmentDate\":\"2024-01-20\",\"grade\":\"A\"}"
```

### Verify in DB

To verify whether all the above data is persisted in the DB
```
http://localhost:8080/h2-console
```