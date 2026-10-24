package com.laundry.systemclass;

public class DigitalPayment extends Payment implements GenerateQR {
    public DigitalPayment(int paymentId, int appointmentId, double amountToBePaid) {
        super(paymentId, appointmentId, amountToBePaid);
    }

    @Override
    public void generateQr() {
        System.out.println("\n+-------------------------+");
        System.out.println("|     QR CODE PAYMENT     |");
        System.out.println("+-------------------------+");
        System.out.println("|       SCAN TO PAY       |");
        System.out.println("+-------------------------+");
        System.out.println("|   [#] [ ] [#] [#] [ ]   |");
        System.out.println("|   [ ] [#] [ ] [#] [#]   |");
        System.out.println("|   [#] [ ] [#] [#] [ ]   |");
        System.out.println("|   [ ] [#] [ ] [#] [#]   |");
        System.out.println("|   [#] [ ] [#] [#] [ ]   |");
        System.out.println("|   [ ] [#] [ ] [#] [#]   |");
        System.out.println("+-------------------------+");
        System.out.println(String.format("| AMOUNT: PHP %-11s |", getAmountToBePaid()));
        System.out.println("+-------------------------+");
    }

    @Override
    public String getPaymentMethod(){
        return "E-Payment";
    }
}