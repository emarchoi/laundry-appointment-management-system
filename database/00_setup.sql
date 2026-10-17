CREATE DATABASE LaundryAppointmentSystemDB
USE LaundryAppointmentSystemDB

CREATE LOGIN laundry_user WITH PASSWORD = 'Laundry';
CREATE USER laundry_user FOR LOGIN laundry_user;
ALTER ROLE db_owner ADD MEMBER laundry_user;