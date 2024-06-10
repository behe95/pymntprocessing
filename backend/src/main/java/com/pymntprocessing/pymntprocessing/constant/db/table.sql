CREATE TABLE Vendor (
    vendorPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    address VARCHAR(100),
    vendorId VARCHAR(50) UNIQUE
);

CREATE TABLE PaymentTransaction (
    paymentTransactionPk INT PRIMARY KEY AUTO_INCREMENT,
    fkTransactionType INT,          -- debit, credit
    fkGLAccount INT,
    transactionNumber INT UNIQUE,
    transactionDescription VARCHAR(100),
    transactionAmount DECIMAL(15,2),
    transactionDate DATETIME,
    created DATETIME,
    updated DATETIME
);




drop table TransactionType;
CREATE TABLE TransactionType (
    transactionTypePk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    multiplier INT,
    code INT UNIQUE
);

INSERT INTO TransactionType(name, multiplier, code)
VALUES
    ('Credit', -1, 1), ('Debit', 1, 2);


CREATE TABLE PaymentInvoice (
    paymentInvoicePk INT PRIMARY KEY AUTO_INCREMENT,
    fkVendor INT,
    fkCheck INT,
    fkInvoiceStatus INT,        -- batched, listed, posted, paid
    fkPaymentTransaction INT,
    invoiceNumber INT UNIQUE,
    invoiceDescription VARCHAR(100),
    invoiceAmount DECIMAL(15,2),
    invoiceDate DATETIME,
    invoiceDueDate DATETIME,
    invoiceReceivedDate DATETIME,
    created DATETIME,
    updated DATETIME
);

CREATE TABLE InvoiceStatus (
    invoiceStatusPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    code INT UNIQUE
);

INSERT INTO InvoiceStatus(name, code)
VALUES
    ('Open', 1), ('Committed', 2), ('Batched', 3), ('Listed', 4), ('Posted', 5), ('Paid', 6);