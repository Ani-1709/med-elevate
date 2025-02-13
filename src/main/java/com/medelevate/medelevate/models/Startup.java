package com.medelevate.medelevate.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private User founder;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String description;

    @Column(nullable=false)
    private String industry;

    @Column(nullable=false)
    private String contact;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false, unique=true)
    private String registrationNumber;

    @Column(nullable=false)
    private String country;

    @Column(nullable=false)
    private Date registrationDate;

    @Column(nullable=false)
    private String status; 

    public Startup() {
        super();
    }

    public Startup(Long id, User founder, String name, String description, String industry, String contact,
                   String address, String registrationNumber, String country, Date registrationDate, String status) {
        this.id = id;
        this.founder = founder;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.contact = contact;
        this.address = address;
        this.registrationNumber = registrationNumber;
        this.country = country;
        this.registrationDate = registrationDate;
        this.status = status;
    }
    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "Pending";
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFounder() {
        return founder;
    }

    public void setFounder(User founder) {
        this.founder = founder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Startup [id=" + id + ", founder=" + founder + ", name=" + name + ", description=" + description
                + ", industry=" + industry + ", contact=" + contact + ", address=" + address + ", registrationNumber="
                + registrationNumber + ", country=" + country + ", registrationDate=" + registrationDate + ", status="
                + status + "]";
    }
}
