package edu.campuspark;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class ApiController {
    private final SlotRepository slots;
    private final UserRepository users;
    private final BookingRepository bookings;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public ApiController(SlotRepository slots,UserRepository users,BookingRepository bookings){this.slots=slots;this.users=users;this.bookings=bookings;}

    record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@NotBlank String password) {}
    record LoginRequest(@Email @NotBlank String email,@NotBlank String password) {}
    record AccountResponse(Long id,String name,String email,String role) {}
    record BookingRequest(@NotNull Long slotId,@NotNull LocalDate date,@NotNull LocalTime time,@NotNull Long userId) {}
    record SlotRequest(@NotBlank String name,@NotBlank String code,@NotBlank String area,@NotBlank String type) {}
    record AvailabilityRequest(boolean available) {}

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    AccountResponse register(@Valid @RequestBody RegisterRequest request){
        if(users.findByEmail(request.email().toLowerCase()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
        CampusUser user=users.save(new CampusUser(request.email().toLowerCase(),request.name(),encoder.encode(request.password()),"STUDENT"));
        return account(user);
    }
    @PostMapping("/auth/login")
    AccountResponse login(@Valid @RequestBody LoginRequest request){
        CampusUser user=users.findByEmail(request.email().toLowerCase()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
        if(!encoder.matches(request.password(),user.passwordHash)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        return account(user);
    }
    private AccountResponse account(CampusUser u){return new AccountResponse(u.id,u.fullName,u.email,u.role);}

    @GetMapping("/slots") List<ParkingSlot> getSlots(@RequestParam(required=false) LocalDate date,@RequestParam(required=false) LocalTime time){
        List<ParkingSlot> result=slots.findAll();
        if(date!=null&&time!=null) result.forEach(s->{if(bookings.existsBySlotIdAndDateAndTimeAndStatus(s.id,date,time,"CONFIRMED"))s.available=false;});
        return result;
    }
    @PostMapping("/bookings") @ResponseStatus(HttpStatus.CREATED)
    ParkingBooking createBooking(@Valid @RequestBody BookingRequest request){
        if(request.date().isBefore(LocalDate.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Booking date must be today or later");
        CampusUser user=users.findById(request.userId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        ParkingSlot slot=slots.findById(request.slotId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Parking spot not found"));
        if(!slot.available || bookings.existsBySlotIdAndDateAndTimeAndStatus(slot.id,request.date(),request.time(),"CONFIRMED")) throw new ResponseStatusException(HttpStatus.CONFLICT,"This parking spot is already booked for that time");
        return bookings.save(new ParkingBooking(user,slot,request.date(),request.time()));
    }
    @GetMapping("/bookings") List<ParkingBooking> getBookings(@RequestParam(required=false) Long userId){return userId==null?bookings.findAllByOrderByDateDescTimeDesc():bookings.findByUserIdOrderByDateAscTimeAsc(userId);}
    @PatchMapping("/bookings/{id}/cancel") ParkingBooking cancelBooking(@PathVariable Long id){
        ParkingBooking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));b.status="CANCELLED";return bookings.save(b);
    }
    @PatchMapping("/bookings/{id}/complete") ParkingBooking completeBooking(@PathVariable Long id){
        ParkingBooking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));b.status="COMPLETED";return bookings.save(b);
    }
    @PostMapping("/admin/slots") @ResponseStatus(HttpStatus.CREATED) ParkingSlot addSlot(@Valid @RequestBody SlotRequest r){
        ParkingSlot s=new ParkingSlot(r.name(),r.code(),r.area(),r.type());return slots.save(s);
    }
    @DeleteMapping("/admin/slots/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void removeSlot(@PathVariable Long id){if(!slots.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Parking spot not found");slots.deleteById(id);}
    @PatchMapping("/admin/slots/{id}") ParkingSlot updateSlot(@PathVariable Long id,@RequestBody AvailabilityRequest r){
        ParkingSlot s=slots.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Parking spot not found"));s.available=r.available();return slots.save(s);
    }
}
