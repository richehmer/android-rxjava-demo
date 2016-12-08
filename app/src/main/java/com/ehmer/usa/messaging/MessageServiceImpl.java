package com.ehmer.usa.messaging;

import com.ehmer.usa.bill.Bill;
import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.constitution.UsConstitution;
import com.ehmer.usa.persistance.BillListPref;
import com.ehmer.usa.persistance.prefs.StringSetPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by Ehmer, R.G. on 12/8/16.
 */

public class MessageServiceImpl implements ConstitutionalMessageService {

    private final ConstitutionService constitutionService;
    private BehaviorSubject<List<Bill>> proposedSubject;
    private BehaviorSubject<List<Bill>> vetoedSubject;
    private BehaviorSubject<List<Bill>> passedSubject;

    private PublishSubject<Bill> billStream = PublishSubject.create();

    @Inject
    public MessageServiceImpl(ConstitutionService constitutionService, @BillListPref final StringSetPreference billsPref, final Gson gson) {
        this.constitutionService = constitutionService;
        constitutionService.getConstitution().subscribe(new Action1<UsConstitution>() {
            @Override
            public void call(UsConstitution constitution) {
                // clear everything
                proposedSubject.onNext(new ArrayList<Bill>());
                vetoedSubject.onNext(new ArrayList<Bill>());
                passedSubject.onNext(new ArrayList<Bill>());
            }
        });


        // populate the subjects
        final List<Bill> proposed = new ArrayList<>();
        final List<Bill> vetoed = new ArrayList<>();
        final List<Bill> passed = new ArrayList<>();
        Observable.from(billsPref.get())
                .map(new Func1<String, Bill>() {
                    @Override
                    public Bill call(String s) {
                        return gson.fromJson(s, Bill.class);
                    }
                })
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<Bill>() {
                    @Override
                    public void call(Bill bill) {
                        if (bill.state == Bill.VETOED) {
                            vetoed.add(bill);
                        } else if (bill.state == Bill.PROPOSED) {
                            proposed.add(bill);
                        } else if (bill.state == Bill.PASSED) {
                            passed.add(bill);
                        }
                    }
                });

        proposedSubject = BehaviorSubject.create(proposed);
        vetoedSubject = BehaviorSubject.create(vetoed);
        passedSubject = BehaviorSubject.create(passed);


        // observe bill stream for changes
        billStream.filter(new Func1<Bill, Boolean>() {
            @Override
            public Boolean call(Bill bill) {
                return bill.state == Bill.PROPOSED;
            }
        }).subscribe(new Action1<Bill>() {
            @Override
            public void call(Bill bill) {
                final List<Bill> proposed = proposedSubject.getValue();
                proposed.add(bill);
                proposedSubject.onNext(proposed);
            }
        });

        // observe bill stream for changes
        billStream.filter(new Func1<Bill, Boolean>() {
            @Override
            public Boolean call(Bill bill) {
                return bill.state == Bill.VETOED;
            }
        }).subscribe(new Action1<Bill>() {
            @Override
            public void call(Bill bill) {
                final List<Bill> proposed = proposedSubject.getValue();
                final List<Bill> vetoed = vetoedSubject.getValue();
                proposed.remove(bill);
                vetoed.add(bill);
                proposedSubject.onNext(proposed);
                vetoedSubject.onNext(vetoed);

            }
        });

        // observe bill stream for changes
        billStream.filter(new Func1<Bill, Boolean>() {
            @Override
            public Boolean call(Bill bill) {
                return bill.state == Bill.PASSED;
            }
        }).subscribe(new Action1<Bill>() {
            @Override
            public void call(Bill bill) {
                final List<Bill> proposed = proposedSubject.getValue();
                final List<Bill> vetoed = vetoedSubject.getValue();
                final List<Bill> passed = passedSubject.getValue();
                if (vetoed.remove(bill)) {
                    vetoedSubject.onNext(vetoed);
                }

                if (proposed.remove(bill)) {
                    proposedSubject.onNext(proposed);
                }

                passed.add(bill);
                passedSubject.onNext(passed);
            }
        });

        // observe bill stream for changes
        billStream.filter(new Func1<Bill, Boolean>() {
            @Override
            public Boolean call(Bill bill) {
                return bill.state == Bill.FAILED;
            }
        }).subscribe(new Action1<Bill>() {
            @Override
            public void call(Bill bill) {
                final List<Bill> proposed = proposedSubject.getValue();
                final List<Bill> vetoed = vetoedSubject.getValue();
                final List<Bill> passed = passedSubject.getValue();
                if (vetoed.remove(bill)) {
                    vetoedSubject.onNext(vetoed);
                }
                if (proposed.remove(bill)) {
                    proposedSubject.onNext(proposed);
                }

                if (passed.remove(bill)) {
                    passedSubject.onNext(passed);
                }
            }
        });

        // save all changes to preferences
        Observable.combineLatest(
                vetoedSubject, proposedSubject, passedSubject,
                new Func3<List<Bill>, List<Bill>, List<Bill>, Set<Bill>>() {
                    @Override
                    public Set<Bill> call(List<Bill> bills, List<Bill> bills2, List<Bill> bills3) {
                        Set<Bill> newSet = new HashSet<>();
                        newSet.addAll(bills);
                        newSet.addAll(bills2);
                        newSet.addAll(bills3);
                        return newSet;
                    }
                })
                .map(new Func1<Set<Bill>, Set<String>>() {
                    @Override
                    public Set<String> call(Set<Bill> bills) {
                        Set<String> stringSet = new HashSet<String>();
                        for (Bill bill : bills) {
                            stringSet.add(gson.toJson(bill));
                        }
                        return stringSet;
                    }
                })
                .distinctUntilChanged()
                .subscribe(new Action1<Set<String>>() {
                    @Override
                    public void call(Set<String> strings) {
                        billsPref.set(strings);
                    }
                });


    }


    @Override
    public void proposeBill(Bill bill) {
        billStream.onNext(new Bill(bill, Bill.PROPOSED));
    }

    @Override
    public void vetoBill(Bill bill) {
        billStream.onNext(new Bill(bill, Bill.VETOED));
    }

    @Override
    public void signBill(Bill bill) {
        billStream.onNext(new Bill(bill, Bill.PASSED));
    }

    @Override
    public void overrideVeto(Bill bill) {
        billStream.onNext(new Bill(bill, Bill.PASSED));
    }

    @Override
    public void invalidateBill(Bill bill) {
        billStream.onNext(new Bill(bill, Bill.FAILED));
    }

    @Override
    public Observable<List<Bill>> proposedBills() {
        return proposedSubject.asObservable();
    }

    @Override
    public Observable<List<Bill>> passedBills() {
        return passedSubject.asObservable();
    }

    @Override
    public Observable<List<Bill>> vetoedBills() {
        return vetoedSubject.asObservable();
    }
}
