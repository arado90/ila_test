package com.ila_test.abdulla_alaradi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EmployeesDB {

    @JsonSerialize
    public record Employee(int id, String firstName, String lastName, String dateOfBirth, String salary,
                           String department, String joinDate) {
    }

    private static String DbPath() { return "data/db.json"; }

    private static List<Employee> getData() throws IOException, JsonParseException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            return mapper.readValue(new File(EmployeesDB.DbPath()), new TypeReference<List<Employee>>() {});
        } catch (Exception e) {
            throw e;
        }
    }

    @Nullable
    public static Employee getEmployee(int id) throws IOException, JsonParseException {
        try {
            List<Employee> employees = EmployeesDB.getData();
            return employees.stream().filter(employee -> employee.id == id).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private static int getNextID() {
        try {
            List<Employee> employees = EmployeesDB.getData();
            AtomicInteger maxId = new AtomicInteger();
            employees.forEach(emp -> {
                if (emp.id > maxId.get()) maxId.set(emp.id);
            });
            return maxId.get()+1;
        } catch (IOException e) {
            return 1;
        }
    }

    public static List<Employee> getEmployees(Optional<String> name, Optional<String> salaryFrom, Optional<String> salaryTo, Optional<String> page, Optional<String> limit) throws IOException, JsonParseException {
        List<Employee> employees = EmployeesDB.getData();
        Predicate<Employee> predicate = employee -> true;
        if (name != null && name.isPresent()) {
            String nameStr = name.get().toLowerCase();
            predicate = predicate.and(employee -> employee.firstName.toLowerCase().contains(nameStr) || employee.lastName.toLowerCase().contains(nameStr));
        }
        if (salaryFrom != null && salaryFrom.isPresent()) {
            predicate = predicate.and(employee -> Double.parseDouble(employee.salary) >= Double.parseDouble(salaryFrom.get()));
        }
        if (salaryTo != null && salaryTo.isPresent()) {
            predicate = predicate.and(employee -> Double.parseDouble(employee.salary) <= Double.parseDouble(salaryTo.get()));
        }
        try {
            Stream<Employee> result = employees.stream().filter(predicate);
            if (limit != null && limit.isPresent()) {
                if (page != null && page.isPresent()) {
                    result = result.skip((long) Integer.parseInt(page.get()) * Integer.parseInt(limit.get()));
                }
                result = result.limit(Integer.parseInt(limit.get()));
            }
            return result.toList();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static int insertEmployee(String firstName, String lastName, String dateOfBirth, String salary, String department, String joinDate) throws IOException {
        Employee employee = new Employee(EmployeesDB.getNextID(), firstName, lastName, dateOfBirth, salary, department, joinDate);
        File file = new File(EmployeesDB.DbPath());
        List<Employee> data = EmployeesDB.getData();
        data.add(employee);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(file, data);
            return employee.id;
        } catch (Exception e) {
            throw e;
        }
    }
}
