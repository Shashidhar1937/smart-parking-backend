package edu.campuspark;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

interface SlotRepository extends JpaRepository<ParkingSlot,Long> { Optional<ParkingSlot> findByCode(String code); }
interface UserRepository extends JpaRepository<CampusUser,Long> { Optional<CampusUser> findByEmail(String email); }
interface BookingRepository extends JpaRepository<ParkingBooking,Long> {
    List<ParkingBooking> findByUserIdOrderByDateAscTimeAsc(Long userId);
    List<ParkingBooking> findAllByOrderByDateDescTimeDesc();
    boolean existsBySlotIdAndDateAndTimeAndStatus(Long slotId,java.time.LocalDate date,java.time.LocalTime time,String status);
}
