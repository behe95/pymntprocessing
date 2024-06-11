DROP TABLE Vendor;
DROP TABLE Invoice;
DROP TABLE PaymentTransaction;
DROP TABLE TransactionType;
DROP TABLE InvoiceStatus;

-- Default Values TransactionType
INSERT INTO TransactionType(name, multiplier, code)
VALUES
    ('Credit', -1, 1), ('Debit', 1, 2);

-- Default Values InvoiceStatus
INSERT INTO InvoiceStatus(name, code)
VALUES
    ('Open', 1), ('Committed', 2), ('Batched', 3), ('Listed', 4), ('Posted', 5), ('Paid', 6);

-- TEST DATA
INSERT INTO Vendor
    (name, address, vendorId)
VALUES
    ("Vendor CO", "123 Main St. , Main City, NY, 02100", "VI00001")
    ,("Test Vendor", "55 Test St. , Test City, CA, 01100", "VI00002");


INSERT INTO PaymentTransaction
    (fkVendor, fkTransactionType, fkGLAccount, fkInvoice, transactionNumber, transactionDescription, transactionAmount, transactionDate, created, modified)
VALUES
    (1, 2, NULL, 1, 1, "Project One - 1/3", 5500.16, NOW(), DATE_SUB(NOW(), INTERVAL 5 DAY), NULL)
    ,(1, 2, NULL, 1, 2, "Project One - 2/3", 1500.85, NOW(), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL);




INSERT INTO Invoice
    (fkVendor, fkCheck, fkInvoiceStatus, invoiceNumber, invoiceDescription, invoiceAmount, invoiceDate, invoiceDueDate, invoiceReceivedDate, created, modified)
VALUES
    (1, NULL, 1, 1, "Invoice One", NULL, NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), NULL, NOW(), NULL)
    ,(1, NULL, 1, 2, "2nd Invoice", NULL, NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, NOW(), NULL);