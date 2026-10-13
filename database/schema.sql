CREATE DATABASE IF NOT EXISTS campus_parking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_parking;

-- Spring Data JPA creates/updates these tables on first launch (ddl-auto=update).
-- This file documents the data model for MySQL installations.
CREATE TABLE IF NOT EXISTS campus_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  full_name VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL DEFAULT 'STUDENT'
);
CREATE TABLE IF NOT EXISTS parking_slots (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(255) NOT NULL UNIQUE,
  area VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  available BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS parking_bookings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  slot_id BIGINT NOT NULL,
  date DATE NOT NULL,
  time TIME NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
  CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES campus_users(id),
  CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id) REFERENCES parking_slots(id),
  INDEX idx_slot_datetime_status (slot_id, date, time, status)
);
