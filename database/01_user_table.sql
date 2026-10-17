USE LaundryAppointmentSystemDB

/* +-------------+
   | USER TABLES |
   +-------------+ */

-- CUSTOMER TABLE
CREATE TABLE Customer(
	ReferenceNumber VARCHAR(30) NOT NULL,
	FirstName VARCHAR(30) NOT NULL,
	LastName VARCHAR(30) NOT NULL,
	PhoneNumber VARCHAR(11) NOT NULL,
	Barangay VARCHAR(50) NOT NULL,
	Street VARCHAR(50) NOT NULL,
	BlockNo VARCHAR(50) NOT NULL,

	CONSTRAINT Customer_PK 
		PRIMARY KEY(ReferenceNumber)
)

-- STAFF TABLE
CREATE TABLE Staff (
    StaffID INT NOT NULL,
    FirstName VARCHAR(30) NOT NULL,
    LastName VARCHAR(30) NOT NULL,
    StaffType VARCHAR(30) NOT NULL,
 
    CONSTRAINT Staff_PK
        PRIMARY KEY (StaffID),
 
    CONSTRAINT StaffType_Check
        CHECK (StaffType IN ('DeliveryRider', 'LaundryAttendant'))
)