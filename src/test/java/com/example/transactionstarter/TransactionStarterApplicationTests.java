package com.example.transactionstarter;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.dto.UpdateTransactionStatusRequest;
import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.enums.TransactionType;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionStarterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

// Clears the database before each test to keep tests independent.
    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

// Tests that a transaction can be created successfully and retrieved from the database.
    @Test
    void testCreateAndGetAllTransactions() throws Exception {

        CreateTransactionRequest request
                = new CreateTransactionRequest();

        request.setTransactionId("TXN-001");
        request.setCustomerId("CUST-001");
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.PAYMENT);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN-001"))
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.transactionType").value("PAYMENT"))
                .andExpect(jsonPath("$.transactionStatus").value("PENDING"));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Tests that a transaction is rejected when required validation data is missing.
    @Test
    void testCreateTransaction_ValidationFailure() throws Exception {

        CreateTransactionRequest request
                = new CreateTransactionRequest();

        request.setCustomerId("CUST-001");
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency(Currency.USD);
        request.setTransactionType(TransactionType.PAYMENT);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.status").value(400));
    }

// Tests that creating a transaction with an existing ID is rejected.
    @Test
    void testCreateTransaction_DuplicateTransactionId() throws Exception {

        CreateTransactionRequest firstRequest
                = new CreateTransactionRequest();

        firstRequest.setTransactionId("TXN-001");
        firstRequest.setCustomerId("CUST-001");
        firstRequest.setAmount(new BigDecimal("150.00"));
        firstRequest.setCurrency(Currency.USD);
        firstRequest.setTransactionType(TransactionType.PAYMENT);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        CreateTransactionRequest duplicateRequest
                = new CreateTransactionRequest();

        duplicateRequest.setTransactionId("TXN-001");
        duplicateRequest.setCustomerId("CUST-002");
        duplicateRequest.setAmount(new BigDecimal("200.00"));
        duplicateRequest.setCurrency(Currency.INR);
        duplicateRequest.setTransactionType(TransactionType.TRANSFER);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Transaction already exists with id: TXN-001"))
                .andExpect(jsonPath("$.status").value(400));
    }

// Tests retrieving a transaction using its transaction ID.
    @Test
    void testGetTransactionById() throws Exception {

        String transactionId = "TXN-002";

        Transaction transaction = new Transaction(
                transactionId,
                "CUST-101",
                new BigDecimal("100.00"),
                Currency.INR,
                TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED
        );

        transactionRepository.save(transaction);

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.customerId").value("CUST-101"));
    }

// Tests retrieving all transactions belonging to a specific customer.
    @Test
    void testGetTransactionsByCustomerId() throws Exception {

        String transactionId1 = "TXN-003";
        String transactionId2 = "TXN-004";

        Transaction t1 = new Transaction(
                transactionId1,
                "CUST-101",
                new BigDecimal("100.00"),
                Currency.INR,
                TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED
        );

        Transaction t2 = new Transaction(
                transactionId2,
                "CUST-102",
                new BigDecimal("200.00"),
                Currency.EUR,
                TransactionType.WITHDRAWAL,
                TransactionStatus.PENDING
        );

        transactionRepository.save(t1);
        transactionRepository.save(t2);

        mockMvc.perform(get("/transactions/customer/CUST-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value(transactionId1))
                .andExpect(jsonPath("$[0].customerId").value("CUST-101"));
    }

// Tests a valid status transition from PENDING to PROCESSING.
    @Test
    void testUpdateTransactionStatus_Success() throws Exception {

        String transactionId = "TXN-005";

        Transaction transaction = new Transaction(
                transactionId,
                "CUST-101",
                new BigDecimal("100.00"),
                Currency.INR,
                TransactionType.DEPOSIT,
                TransactionStatus.PENDING
        );

        transactionRepository.save(transaction);

        UpdateTransactionStatusRequest request
                = new UpdateTransactionStatusRequest();

        request.setTransactionStatus(TransactionStatus.PROCESSING);

        mockMvc.perform(patch("/transactions/" + transactionId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.transactionStatus").value("PROCESSING"));
    }

// Tests that an invalid status transition is rejected.
    @Test
    void testUpdateTransactionStatus_InvalidTransition() throws Exception {

        String transactionId = "TXN-006";

        Transaction transaction = new Transaction(
                transactionId,
                "CUST-101",
                new BigDecimal("100.00"),
                Currency.INR,
                TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED
        );

        transactionRepository.save(transaction);

        UpdateTransactionStatusRequest request
                = new UpdateTransactionStatusRequest();

        request.setTransactionStatus(TransactionStatus.PENDING);

        mockMvc.perform(patch("/transactions/" + transactionId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

// Tests that requesting transactions for an unknown customer returns 404.
    @Test
    void testGetTransactionsByCustomerId_NotFound() throws Exception {

        mockMvc.perform(get("/transactions/customer/NON_EXISTENT_CUST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No transactions found for customer id: NON_EXISTENT_CUST"))
                .andExpect(jsonPath("$.status").value(404));
    }

// Tests that requesting a transaction with an unknown ID returns 404.
    @Test
    void testGetTransactionById_NotFound() throws Exception {

        mockMvc.perform(get("/transactions/NON_EXISTENT_TXN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Transaction not found with id: NON_EXISTENT_TXN"))
                .andExpect(jsonPath("$.status").value(404));
    }

}
