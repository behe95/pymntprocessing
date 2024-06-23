package com.pymntprocessing.pymntprocessing.integration;

import com.pymntprocessing.pymntprocessing.model.entity.*;
import com.pymntprocessing.pymntprocessing.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Component
public class DatabaseInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InvoiceStatusRepository invoiceStatusRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    public static TransactionType transactionType;
    public static InvoiceStatus invoiceStatus;
    public static Vendor vendor1;
    public static Vendor vendor2;
    public static Product product1;
    public static Product product2;

    public static PaymentTransaction paymentTransaction;
    public static Invoice invoice;

    public static LocalDateTime datetime = LocalDateTime.of(2024, Month.JANUARY, 15, 10, 30, 30, 653132);


    @Transactional
    public void init() {
        this.preConfig();

        /**
         * Transaction Type
         */
        transactionType = new TransactionType();
        transactionType.setId(2L);
        transactionType.setName("Debit");
        transactionType.setMultiplier(1);
        transactionType.setCode(2);

        /**
         * Invoice Status
         */
        invoiceStatus = new InvoiceStatus();
        invoiceStatus.setId(1L);
        invoiceStatus.setName("Open");
        invoiceStatus.setCode(1);

        /**
         * Vendor
         */
        vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor CO");
        vendor1.setAddress("123 Main St. , Main City, NY, 02100");
        vendor1.setVendorId("VI00001");

        vendor2 = new Vendor();
        vendor2.setId(2L);
        vendor2.setName("Test Vendor");
        vendor2.setAddress("55 Test St. , Test City, CA, 01100");
        vendor2.setVendorId("VI00002");

        /**
         * Product
         */
        product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Product One");
        product1.setProductDescription("Product One Description");
        product1.setCreated(datetime);
        product1.setModified(null);

        product2 = new Product();
        product2.setId(2L);
        product2.setPaymentTransactions(List.of());
        product2.setProductName("Product Two");
        product2.setProductDescription("Product Two Description Test Data");
        product2.setCreated(datetime);
        product2.setModified(null);

        /**
         * Payment Transaction
         */

        paymentTransaction = new PaymentTransaction();
        paymentTransaction.setId(1L);
        paymentTransaction.setProduct(product1);
        paymentTransaction.setVendor(vendor1);
        paymentTransaction.setTransactionType(transactionType);
        paymentTransaction.setTransactionNumber(1);
        paymentTransaction.setTransactionDescription("Project One - 1/3");
        paymentTransaction.setTransactionAmount(5500.16);

        // Bi-Directional -- persist entity
        product1.setPaymentTransactions(List.of(paymentTransaction));

        /**
         * Invoice
         */
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(invoiceStatus);
        invoice.setVendor(vendor1);
        invoice.setPaymentTransaction(List.of(paymentTransaction));
        invoice.setInvoiceNumber(1);
        invoice.setInvoiceDescription("Invoice One");
        invoice.setInvoiceAmount(null);
        invoice.setInvoiceDate(datetime.minusDays(2));
        invoice.setInvoiceDueDate(datetime.plusDays(2));
        invoice.setInvoiceReceivedDate(null);
        invoice.setCreated(datetime);
        invoice.setModified(null);


        this.invoiceRepository.deleteAll();
        this.paymentTransactionRepository.deleteAll();
        this.productRepository.deleteAll();
        this.vendorRepository.deleteAll();

        /**
         * Reset the PK counter to 1
         */
        this.entityManager.createNativeQuery("ALTER TABLE Product AUTO_INCREMENT = 1;").executeUpdate();
        this.entityManager.createNativeQuery("ALTER TABLE PaymentTransaction AUTO_INCREMENT = 1;").executeUpdate();
        this.entityManager.createNativeQuery("ALTER TABLE Vendor AUTO_INCREMENT = 1;").executeUpdate();
        this.entityManager.createNativeQuery("ALTER TABLE Invoice AUTO_INCREMENT = 1;").executeUpdate();


        this.vendorRepository.saveAll(List.of(vendor1, vendor2));
        this.productRepository.saveAll(List.of(product1, product2));
        this.paymentTransactionRepository.saveAll(List.of(paymentTransaction));
        this.invoiceRepository.saveAll(List.of(invoice));
    }

    @Transactional
    public void preConfig() {
        /**
         * Transaction Type
         */
        TransactionType transactionType1 = new TransactionType();
        transactionType1.setId(1L);
        transactionType1.setName("Credit");
        transactionType1.setMultiplier(-1);
        transactionType1.setCode(1);

        TransactionType transactionType2 = new TransactionType();
        transactionType2.setId(2L);
        transactionType2.setName("Debit");
        transactionType2.setMultiplier(1);
        transactionType2.setCode(2);

        /**
         * InvoiceStatus Type
         */
        InvoiceStatus invoiceStatus1 = new InvoiceStatus();
        invoiceStatus1.setId(1L);
        invoiceStatus1.setName("Open");
        invoiceStatus1.setCode(1);

        InvoiceStatus invoiceStatus2 = new InvoiceStatus();
        invoiceStatus2.setId(2L);
        invoiceStatus2.setName("Committed");
        invoiceStatus2.setCode(2);

        InvoiceStatus invoiceStatus3 = new InvoiceStatus();
        invoiceStatus3.setId(3L);
        invoiceStatus3.setName("Batched");
        invoiceStatus3.setCode(3);

        InvoiceStatus invoiceStatus4 = new InvoiceStatus();
        invoiceStatus4.setId(4L);
        invoiceStatus4.setName("Listed");
        invoiceStatus4.setCode(4);

        InvoiceStatus invoiceStatus5 = new InvoiceStatus();
        invoiceStatus5.setId(5L);
        invoiceStatus5.setName("Posted");
        invoiceStatus5.setCode(5);


        InvoiceStatus invoiceStatus6 = new InvoiceStatus();
        invoiceStatus6.setId(6L);
        invoiceStatus6.setName("Paid");
        invoiceStatus6.setCode(6);




        this.invoiceStatusRepository.deleteAll();
        this.transactionTypeRepository.deleteAll();

        /**
         * Reset the PK counter to 1
         */
        this.entityManager.createNativeQuery("ALTER TABLE InvoiceStatus AUTO_INCREMENT = 1;").executeUpdate();
        this.entityManager.createNativeQuery("ALTER TABLE TransactionType AUTO_INCREMENT = 1;").executeUpdate();


        this.invoiceStatusRepository.saveAll(List.of(invoiceStatus1, invoiceStatus2, invoiceStatus3, invoiceStatus4, invoiceStatus5, invoiceStatus6));
        this.transactionTypeRepository.saveAll(List.of(transactionType1, transactionType2));

    }
}
