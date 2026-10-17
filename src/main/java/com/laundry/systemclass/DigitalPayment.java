package com.laundry.systemclass;

public class DigitalPayment extends Payment implements GenerateQR {
    public DigitalPayment(int paymentId, int appointmentId, double amountToBePaid) {
        super(paymentId, appointmentId, amountToBePaid);
    }

    @Override
    public String generateQr() {
        return "====================================\n" +
               "|  [SCAN THIS QR CODE TO PAY]      |\n" +
               "|  AMOUNT: PHP " + getAmountToBePaid() + "              |\n" +
               "====================================";
    }

    @Override
    public String getPaymentMethod(){
        return "E-Payment";
    }
}