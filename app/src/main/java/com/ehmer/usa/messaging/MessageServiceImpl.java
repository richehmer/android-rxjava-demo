package com.ehmer.usa.messaging;

import com.ehmer.usa.bill.Bill;
import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.constitution.UsConstitution;
import com.ehmer.usa.persistance.BillListPref;
import com.ehmer.usa.persistance.prefs.StringSetPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Created by Ehmer, R.G. on 12/8/16.
 */

public class MessageServiceImpl implements ConstitutionalMessageService {

    /**
     * Holds all current bills and emits the cached bill immediately. This subject
     * will always have a cached set of bills.
     */
    private final BehaviorSubject<List<Bill>> billStream;

    @Inject
    public MessageServiceImpl(ConstitutionService constitutionService,
                              @BillListPref final StringSetPreference billsPref, final Gson gson) {

        // populate the bill stream subject with it's first cached value
        HashSet<Bill> cachedBills = new LinkedHashSet<>();
        for (String rawBill : billsPref.get()) {
            cachedBills.add(gson.fromJson(rawBill, Bill.class));
        }
        final List<Bill> cached = new ArrayList<>(cachedBills);
        billStream = BehaviorSubject.create(cached);

        // save all updates to the cache (skip the first cached emission we populated above)
        billStream.skip(1).subscribe(new Action1<List<Bill>>() {
            @Override
            public void call(List<Bill> bills) {
                HashSet<String> saved = new LinkedHashSet<>();
                for (Bill bill : bills) {
                    saved.add(gson.toJson(bill));
                }
                billsPref.set(saved);
            }
        });

        // clear the bills if the constitution isn't ratified
        constitutionService.getConstitution().subscribe(new Action1<UsConstitution>() {
            @Override
            public void call(UsConstitution constitution) {
                // clears everything
                if (!constitution.isRatified()) {
                    billStream.onNext(new ArrayList<Bill>());
                }
            }
        });
    }

    @Override
    public Observable<List<Bill>> proposedBills() {
        return billStream.asObservable()
                .compose(transformToType(Bill.PROPOSED));
    }

    @Override
    public Observable<List<Bill>> passedBills() {
        return billStream.asObservable()
                .compose(transformToType(Bill.PASSED));
    }

    @Override
    public Observable<List<Bill>> vetoedBills() {
        return billStream.asObservable()
                .compose(transformToType(Bill.VETOED));
    }

    @Override
    public void proposeBill(Bill bill) {
        notifyBillUpdated(new Bill(bill, Bill.PROPOSED));
    }

    @Override
    public void vetoBill(Bill bill) {
        notifyBillUpdated(new Bill(bill, Bill.VETOED));
    }

    @Override
    public void signBill(Bill bill) {
        notifyBillUpdated(new Bill(bill, Bill.PASSED));
    }

    @Override
    public void overrideVeto(Bill bill) {
        notifyBillUpdated(new Bill(bill, Bill.PASSED));
    }

    @Override
    public void invalidateBill(Bill bill) {
        notifyBillUpdated(new Bill(bill, Bill.FAILED));
    }

    /**
     * @param bill new version of the bill
     */
    private synchronized void notifyBillUpdated(Bill bill) {
        final List<Bill> bills = billStream.getValue();
        bills.remove(bill);
        bills.add(bill);
        billStream.onNext(bills);
    }

    /**
     * Transforms an observable that emits lists of bills into one that
     * emits lists of bills that are of the argument bill state
     *
     * @param billState desired bill state
     * @return a {@link rx.Observable.Transformer} that can be used with the 'compose' method
     */
    private static Observable.Transformer<List<Bill>, List<Bill>> transformToType(final int billState) {
        return new Observable.Transformer<List<Bill>, List<Bill>>() {
            @Override
            public Observable<List<Bill>> call(Observable<List<Bill>> listObservable) {
                return listObservable.flatMap(new Func1<List<Bill>, Observable<List<Bill>>>() {
                    @Override
                    public Observable<List<Bill>> call(List<Bill> bills) {
                        return Observable.from(bills)
                                .filter(new Func1<Bill, Boolean>() {
                                    @Override
                                    public Boolean call(Bill bill) {
                                        return bill.state == billState;
                                    }
                                })
                                .toList();
                    }
                });
            }
        };
    }


}
