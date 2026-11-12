
#### AttendantMenu.java - viewPendingAppointments
```
for (Appointment appt : pendingList) {
            int orderCount = orderDAO.getOrdersByAppointment(appt.getAppointmentId()).size();

            System.out.println(String.format("| Appt ID: %-3d | Ref: %-6s | %-7s | Orders: %-2d ",
                    appt.getAppointmentId(),
                    appt.getReferenceNumber(),
                    appt.getAppointmentType(),
                    orderCount));
        }
        System.out.println("+---------------------------------------------------------+");
```

#### AttendantMenu.java - viewAllAppointments
```
private void viewAllAppointments() {
        System.out.println("\n+---------------------------------------------------------------+");
        System.out.println("|                     VIEW ALL APPOINTMENTS                     |");
        System.out.println("+---------------------------------------------------------------+");

        // Retrieve all appointments in the database and store it in a list
        List<Appointment> allAppts = appointmentDAO.getAllAppointments();

        if (allAppts.isEmpty()) {
            System.out.println("\n+---------------------------------------------------------------+");
            System.out.println("| No appointments found in database.                            |");
            System.out.println("+---------------------------------------------------------------+");
        }

        // Loop through each appointment and list its summary details
        for (Appointment appt : allAppts) {
            Customer customer = customerDAO.getCustomerDetails(appt.getReferenceNumber());
            String customerName = (customer != null) ? customer.getFullName() : "N/A";

            System.out.println("+---------------------------------------------------------------+");
            System.out.println(String.format("| Customer : %s", customerName));
            System.out.println("+---------------------------------------------------------------+");
            System.out.println(String.format("| Appt ID: %-6d | Ref: %-9s | Status: %-12s ",
                    appt.getAppointmentId(), appt.getReferenceNumber(), appt.getAppointmentStatus()));
            System.out.println(String.format("| Type: %-9s | ECF: %-9s | Price: PHP%-5.2f | %s",
                    appt.getAppointmentType(), (appt.getECF() > 0 ? appt.getECF() + " mins" : "0 mins"),
                    appt.getAppointmentTotalPrice(), (appt.isPaid() ? "Paid" : "Pending")));

            // Fetch and summary list of orders corresponding to the now looped appointment
            List<LaundryOrder> orders = orderDAO.getOrdersByAppointment(appt.getAppointmentId());
            System.out.println("+---------------------------------------------------------------+");
            System.out.println("| Orders Summary:                                               |");
            System.out.println("+---------------------------------------------------------------+");
            for (int i = 0; i < orders.size(); i++) {
                LaundryOrder o = orders.get(i);
                System.out.println(String.format("| %d. Order #%-6d | %-12s | Weight: %-4.1fkg ",
                        (i + 1), o.getOrderId(), o.getLaundryService(), o.getLaundryTotalKg()));
            }
            System.out.println("+---------------------------------------------------------------+\n");
        }
    } // end of [2] viewAllAppointments()
}
```