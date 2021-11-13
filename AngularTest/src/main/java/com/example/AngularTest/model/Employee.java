package com.example.AngularTest.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,updatable = false)
    @Getter
    private long id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String jobTitle;
    @Getter @Setter
    private String imageUrl;
    @Getter @Setter
    @Column(nullable = false,updatable = false)
    private String employeeCode;

    public Employee(){}


    public Employee(String name, String email, String jobTitle, String imageUrl, String employeeCode) {
        this.name = name;
        this.email = email;
        this.jobTitle = jobTitle;
        this.imageUrl = imageUrl;
        this.employeeCode = employeeCode;
    }


}
