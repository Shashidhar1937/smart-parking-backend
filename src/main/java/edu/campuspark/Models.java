package edu.campuspark;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="parking_slots")
class ParkingSlot {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String name;
    @Column(nullable=false, unique=true) public String code;
    @Column(nullable=false) public String area;
    @Column(nullable=false) public String type;
    @Column(nullable=false) public boolean available = true;
    protected ParkingSlot() {}
    ParkingSlot(String name,String code,String area,String type){this.name=name;this.code=code;this.area=area;this.type=type;}
}

@Entity
@Table(name="campus_users")
class CampusUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false, unique=true) public String email;
    @Column(nullable=false) public String fullName;
    @JsonIgnore @Column(nullable=false) public String passwordHash;
    @Column(nullable=false) public String role="STUDENT";
    protected CampusUser() {}
    CampusUser(String email,String fullName,String passwordHash,String role){this.email=email;this.fullName=fullName;this.passwordHash=passwordHash;this.role=role;}
}

@Entity
@Table(name="parking_bookings")
class ParkingBooking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public CampusUser user;
    @ManyToOne(optional=false) public ParkingSlot slot;
    @Column(nullable=false) public LocalDate date;
    @Column(nullable=false) public LocalTime time;
    @Column(nullable=false) public String status="CONFIRMED";
    protected ParkingBooking() {}
    ParkingBooking(CampusUser user,ParkingSlot slot,LocalDate date,LocalTime time){this.user=user;this.slot=slot;this.date=date;this.time=time;}
}
