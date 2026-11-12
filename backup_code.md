
#### AttendantMenu.java - viewPendingAppointment
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