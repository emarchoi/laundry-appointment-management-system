USE LaundryAppointmentSystemDB

/* +-----------------------+
   | LAUNDRY SYSTEM TABLES |
   +-----------------------+ */

-- APPOINTMENT TABLE
CREATE TABLE Appointment (
    AppointmentID INT NOT NULL,
    ReferenceNumber VARCHAR(30) NOT NULL,
    StaffID INT NULL,      
    AppointmentType VARCHAR(30) NOT NULL,
    AppointmentTotalPrice DECIMAL(10, 2) NOT NULL DEFAULT(0.00),
    AppointmentStatus VARCHAR(30) NOT NULL DEFAULT ('Pending'),
    AppointmentDate DATE NOT NULL DEFAULT(getdate()),
    EstimatedCompletionFinished INT NULL,      
 
    CONSTRAINT Appointment_PK
        PRIMARY KEY (AppointmentID),
 
    CONSTRAINT Appointment_Customer_FK
        FOREIGN KEY (ReferenceNumber)
        REFERENCES Customer(ReferenceNumber),
 
    CONSTRAINT Appointment_Staff_FK
        FOREIGN KEY (StaffID)
        REFERENCES Staff(StaffID),
 
    CONSTRAINT AppointmentType_Check
        CHECK (AppointmentType IN('Walk-In', 'Delivery')),
 
    CONSTRAINT AppointmentStatus_Check
        CHECK (AppointmentStatus IN ('Pending', 'Confirmed', 'Cancelled',
                                     'In Progress', 'Ready for Pickup',
                                     'Out for Delivery', 'Completed'))
)

-- LAUNDRY ORDER TABLE
CREATE TABLE LaundryOrder (
    OrderID INT NOT NULL,
    AppointmentID INT NOT NULL,
    LaundryService VARCHAR(30) NOT NULL,
    LaundryTotalKg DECIMAL(5, 2) NOT NULL DEFAULT(0.00),
    OrderTotalPrice DECIMAL(10,2) NOT NULL DEFAULT(0.00),
    MachinesUsed INT NOT NULL ,
 
    CONSTRAINT LaundryOrder_PK
        PRIMARY KEY (OrderID),
 
    CONSTRAINT LaundryOrder_Appointment_FK
        FOREIGN KEY (AppointmentID)
        REFERENCES Appointment (AppointmentID),
 
    CONSTRAINT LaundryService_Check
        CHECK (LaundryService IN ('Wash', 'Dry', 'Wash and Dry'))   
)

-- PAYMENT TABLE
CREATE TABLE Payment (
    PaymentID INT NOT NULL,
    AppointmentID INT NOT NULL,
    AmountPaid DECIMAL(10, 2) NOT NULL DEFAULT(0.00),
    PaymentMethod VARCHAR(30) NOT NULL DEFAULT ('Not Selected'),
    PaymentStatus VARCHAR(30) NOT NULL DEFAULT ('Pending'),
 
    CONSTRAINT Payment_PK
        PRIMARY KEY (PaymentID),
 
    CONSTRAINT Payment_Appointment_Unique
        UNIQUE (AppointmentID),
 
    CONSTRAINT Payment_Appointment_FK
        FOREIGN KEY (AppointmentID)
        REFERENCES Appointment (AppointmentID),
 
    CONSTRAINT PaymentMethod_Check
        CHECK (PaymentMethod IN ('Not Selected', 'Cash', 'E-Payment')),
 
    CONSTRAINT PaymentStatus_Check
        CHECK (PaymentStatus IN ('Pending', 'Paid'))
)

-- MACHINE TABLE
CREATE TABLE Machine (
    MachineID INT NOT NULL,
    MachineType VARCHAR(30) NOT NULL,
    MaxCapacityKg DECIMAL(5, 2) NOT NULL DEFAULT(7.00),
    BasePrice DECIMAL(10, 2) NOT NULL,
    MachineStatus VARCHAR(30) NOT NULL DEFAULT('Available'),
 
    CONSTRAINT Machine_PK
        PRIMARY KEY (MachineID),
 
    CONSTRAINT MachineType_Check
        CHECK (MachineType IN ('Washer', 'Dryer')),
 
    CONSTRAINT MachineStatus_Check
        CHECK (MachineStatus IN ('Available', 'In Use', 'Under Maintenance')),

    CONSTRAINT MaxCapacityKg_Check
        CHECK (MaxCapacityKg = 7),
    
    CONSTRAINT MachineBasePrice_Check
        CHECK ((MachineType = 'Washer' AND BasePrice = 50)
           OR (MachineType = 'Dryer' AND BasePrice = 65))
)

-- MACHINE USAGE BRIDGE TABLE
CREATE TABLE MachineUsage (
    OrderID INT NOT NULL,
    MachineID INT NOT NULL,
    LaundryLoadKg DECIMAL(5, 2) NOT NULL DEFAULT (0.00),
    PriceCharged DECIMAL(10, 2) NOT NULL DEFAULT (0.00),
    LaundryStartTime TIME NOT NULL DEFAULT(getdate()), 
    LaundryDuration INT NOT NULL DEFAULT (0), 
 
    CONSTRAINT MachineUsage_CPK
        PRIMARY KEY (OrderID, MachineID),
 
    CONSTRAINT MachineUsage_LaundryOrder_FK
        FOREIGN KEY (OrderID)
        REFERENCES LaundryOrder (OrderID),
 
    CONSTRAINT MachineUsage_Machine_FK
        FOREIGN KEY (MachineID)
        REFERENCES Machine (MachineID),

    CONSTRAINT LaundryLoadKg_Check
        CHECK (LaundryLoadKg >= 0 AND LaundryLoadKg <= 7),
 
    CONSTRAINT LaundryDuration_Check
        CHECK (LaundryDuration >= 0)
)