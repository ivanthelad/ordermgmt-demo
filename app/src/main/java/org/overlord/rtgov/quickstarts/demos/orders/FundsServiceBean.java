package org.overlord.rtgov.quickstarts.demos.orders;

import org.overlord.rtgov.common.infinispan.service.InfinispanCacheManager;
import org.overlord.rtgov.internal.ep.DefaultEPContext;
import org.switchyard.component.bean.Service;

import java.util.HashMap;
import java.util.Map;

@Service(FundsService.class)
public class FundsServiceBean implements FundsService {


    InfinispanCacheManager man;


    public FundsServiceBean() {
        man = new InfinispanCacheManager();
        try {
            man.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FundCheck checkfunds(FundCheck order) throws InsufficientFundsException {
        Map<String, Map> principals = man.getCache("Principals");

        if (!order.getOrder().getCustomer().equalsIgnoreCase("Fred"))
            return order;
        Map principal = principals.get(order.getOrder().getCustomer());

        if (principal == null) {
            principal = new java.util.HashMap();
        }

        Double current = (Double) principal.get("exposure");

        if (current == null)
            current = Double.valueOf(0);


        Double newtotal = (order.getItem().getUnitPrice() * order.getOrder().getQuantity());
        if (newtotal == null)
            newtotal = Double.valueOf(0);
        newtotal = (current + newtotal);
        System.out.println("New Total " + newtotal + ", current " + current);

        if (newtotal > 150 && current <= 150) {
         //   principal.put("suspended", Boolean.TRUE);
          //   principal.put("exposure", newtotal);
            System.out.println(" ***  has not enough funds to purchase, " + order.getOrder().getCustomer());


            principals.put(order.getOrder().getCustomer(), principal);
            throw new InsufficientFundsException("Customer [" + order.getOrder().getCustomer() + "] has not enough funds to purchase", order.getOrder().getCustomer());
        }

        principal.put("exposure", newtotal);
        principals.put(order.getOrder().getCustomer(), principal);

        return order;
    }

    public Payment makePayment(Payment payment) {
        Map<String, Map> principals = man.getCache("Principals");
        Map principal = principals.get(payment.getCustomer()
        );

        if (principal == null) {
            principal = new java.util.HashMap();
        }

        Double current = (Double) principal.get("exposure");
        if (current == null) {
            current = Double.valueOf(0);
        }
        Double newamount = Double.valueOf(0);

        if (current > payment.getAmount()) {
            newamount = (current - payment.getAmount());
        }


        if (newamount <= 150 && current > 150) {
            principal.put("suspended", Boolean.FALSE);
        }

        principal.put("exposure", newamount);


        principals.put(payment.getCustomer(), principal);
        return payment;
    }
}
