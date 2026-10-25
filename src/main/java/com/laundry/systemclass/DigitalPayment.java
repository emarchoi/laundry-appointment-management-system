package com.laundry.systemclass;

/*
    Handles digital payment verification and QR code display.
    Inherits from Cash abtract class.
*/
public class DigitalPayment extends Payment implements GenerateQR {
    public DigitalPayment(int paymentId, int appointmentId, double amountToBePaid) {
        super(paymentId, appointmentId, amountToBePaid);
    }

    // Implements the GenerateQR Interface.
    // Overrides its required methods.
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

    // Method overriding: Polymorphism
    // Provides the implementation of the abstract method in Cash.
    @Override
    public String getPaymentMethod(){
        return "E-Payment";
    }
}