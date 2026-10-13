package edu.campuspark;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
class SeedData implements CommandLineRunner {
    private final SlotRepository slots; private final UserRepository users;
    SeedData(SlotRepository slots,UserRepository users){this.slots=slots;this.users=users;}
    public void run(String... args){
        if(slots.count()==0){
            slots.save(new ParkingSlot("North Lot A","N-24","North campus · Main entrance","Open air"));
            slots.save(new ParkingSlot("Library Garage","L-08","Central campus · Library","Covered"));
            slots.save(new ParkingSlot("East Faculty Lot","E-16","East campus · Faculty wing","Open air"));
            slots.save(new ParkingSlot("South Visitor Lot","S-11","South campus · Welcome center","Accessible"));
            slots.save(new ParkingSlot("Science Center","SC-05","Central campus · Science block","Covered"));
            slots.save(new ParkingSlot("Athletics Lot","A-32","West campus · Sports complex","Open air"));
        }
        if(users.findByEmail("student@campus.edu").isEmpty()) users.save(new CampusUser("student@campus.edu","Jordan Davis",new BCryptPasswordEncoder().encode("Campus123!"),"STUDENT"));
        if(users.findByEmail("admin@campus.edu").isEmpty()) users.save(new CampusUser("admin@campus.edu","Campus Administrator",new BCryptPasswordEncoder().encode("Admin123!"),"ADMIN"));
    }
}
