package com.ehmer.usa.messaging;

import com.ehmer.usa.bill.Bill;

import java.util.List;

import rx.Observable;

/**
 * Created by Ehmer, R.G. on 12/8/16.
 */

public interface ConstitutionalMessageService {

    /**
     * Send a bill to the president to sign.
     */
    void proposeBill(Bill bill);

    void vetoBill(Bill bill);

    void signBill(Bill bill);

    void overrideVeto(Bill bill);

    void invalidateBill(Bill bill);

    /**
     * Bills proposed by legislature
     */
    Observable<List<Bill>> proposedBills();

    /**
     * Bills that have been signed by president, or passed with 2/3 majority to override
     * presidential veto.
     */
    Observable<List<Bill>> passedBills();

    /**
     * Bills that have been vetoed by the president.
     */
    Observable<List<Bill>> vetoedBills();

}
