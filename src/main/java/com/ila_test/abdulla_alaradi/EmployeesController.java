package com.ila_test.abdulla_alaradi;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    @GetMapping("/{id}")
    public Object get(@PathVariable int id) {
        try {
            EmployeesDB.Employee employee = EmployeesDB.getEmployee(id);
            if (employee != null) return employee;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Error", "Employee with given ID was not found");
            return map;
        } catch (Exception e) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Error", "Exception raised while fetching employee data");
            map.put("Message", e.getMessage());
            return map;
        }
    }

    @PostMapping({"", "/"})
    public Object create(
        @RequestBody() EmployeesDB.Employee employee
    ) {
        try {
            int id = EmployeesDB.insertEmployee(employee.firstName(), employee.lastName(), employee.dateOfBirth(), employee.salary(), employee.department(), employee.joinDate());
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put("id", id);
            return map;
        } catch (Exception e) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Error", "Exception raised while inserting employee");
            map.put("Message", e.getMessage());
            return map;
        }
    }

    @GetMapping({"", "/"})
    public Object list(
        @RequestParam(name = "name", required = false) Optional<String> name,
        @RequestParam(name = "salaryFrom", required = false) Optional<String> salaryFrom,
        @RequestParam(name = "salaryTo", required = false) Optional<String> salaryTo,
        @RequestParam(name = "page", required = false) Optional<String> page,
        @RequestParam(name = "limit", required = false) Optional<String> limit
    ) {
        try {
            return EmployeesDB.getEmployees(name, salaryFrom, salaryTo, page, limit);
        } catch (Exception e) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Error", "Exception raised while fetching employees data");
            map.put("Message", e.getMessage());
            return map;
        }
    }
}