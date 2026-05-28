package day4.javaspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service demonstrating the self-invocation issue in Spring AOP.
 */
@Service
public class TransactionalService {

    @Autowired
    @Lazy
    private TransactionalService self; // Self-injection for workaround 2

    private final HelperService helperService; // Clean refactoring solution

    public TransactionalService(HelperService helperService) {
        this.helperService = helperService;
    }

    /**
     * BUG SCENARIO: Self-invocation.
     * Calling a transactional method from within the same class bypasses the AOP proxy.
     * The transaction WILL NOT start because the target is invoked on 'this' directly.
     */
    public void processPaymentSelfInvocation(Long recordId, double amount) {
        System.out.println("Processing billing step (bypassing proxy)...");
        // BUG: Under the hood, this compiles to: this.saveBillingRecord(recordId, amount);
        saveBillingRecord(recordId, amount); 
    }

    /**
     * WORKAROUND 2: Invoking via self-injected proxy bean.
     * This uses the self-injected proxy to ensure the call goes through Spring AOP.
     */
    public void processPaymentSelfWorkaround(Long recordId, double amount) {
        System.out.println("Processing billing step (using self-injected proxy)...");
        self.saveBillingRecord(recordId, amount); // Success: transaction starts!
    }

    /**
     * CLEAN SOLUTION (Recommended): Delegating to a helper Spring component.
     */
    public void processPaymentClean(Long recordId, double amount) {
        System.out.println("Processing billing step (delegating to helper component)...");
        helperService.saveBillingRecord(recordId, amount); // Success: transaction starts!
    }

    @Transactional
    public void saveBillingRecord(Long recordId, double amount) {
        System.out.println("Saving billing record to Database under active transaction transaction boundaries...");
        // DB writing logic goes here
    }
}
