package org.overlord.rtgov.quickstarts.demos.orders;

public interface FundsService {


    public FundCheck checkfunds(FundCheck fundCheck)throws InsufficientFundsException ;
    public Payment makePayment(Payment payment);
}
