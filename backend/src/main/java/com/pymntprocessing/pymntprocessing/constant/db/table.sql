CREATE TABLE Vendor (
    vendorPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    address VARCHAR(100),
    vendorId VARCHAR(50) UNIQUE
);

CREATE TABLE PaymentTransaction (
    paymentTransactionPk INT PRIMARY KEY AUTO_INCREMENT,
    fkProduct INT,
    fkVendor INT,
    fkTransactionType INT,          -- debit, credit
    fkGLAccount INT,
    fkInvoice INT,
    transactionNumber INT UNIQUE,
    transactionDescription VARCHAR(100),
    transactionAmount DECIMAL(15,2),
    transactionDate DATETIME,
    created DATETIME,
    modified DATETIME
);




CREATE TABLE TransactionType (
    transactionTypePk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    multiplier INT,
    code INT UNIQUE
);


CREATE TABLE Invoice (
    invoicePk INT PRIMARY KEY AUTO_INCREMENT,
    fkVendor INT,
    fkCheck INT,
    fkInvoiceStatus INT,        -- batched, listed, posted, paid
    invoiceNumber INT UNIQUE,
    invoiceDescription VARCHAR(100),
    invoiceAmount DECIMAL(15,2),
    invoiceDate DATETIME,
    invoiceDueDate DATETIME,
    invoiceReceivedDate DATETIME,
    created DATETIME,
    modified DATETIME
);

CREATE TABLE InvoiceStatus (
    invoiceStatusPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    code INT UNIQUE
);


CREATE TABLE Product (
    productPk INT PRIMARY KEY AUTO_INCREMENT,
    productName VARCHAR(100),
    productDescription VARCHAR(255),
    created DATETIME,
    modified DATETIME
);
