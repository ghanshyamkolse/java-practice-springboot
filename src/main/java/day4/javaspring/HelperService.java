package day4.javaspring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Isolated Helper Service class to hold Spring transactional logic.
 * Extracting transactional concerns into distinct helper components is the recommended
 * design pattern to prevent self-invocation issues.
 */
@Service
public class HelperService {

    /**
     * Executes inside a transactional boundary.
     * When invoked from TransactionalService, the request goes through HelperService's proxy.
     */
    @Transactional
    public void saveBillingRecord(Long recordId, double amount) {
        System.out.println("Helper: Beginning transactional execution...");
        System.out.println("Helper: Saving record " + recordId + " for amount " + amount + " to DB.");
        // Database logic goes here
    }
}
