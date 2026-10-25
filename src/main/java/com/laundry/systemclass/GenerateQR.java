package com.laundry.systemclass;

/*
    Interface that defines the contract for digital payments.
    Digital payments must provide a method to generate a QR payment.
*/

public interface GenerateQR {
    void generateQr();
}