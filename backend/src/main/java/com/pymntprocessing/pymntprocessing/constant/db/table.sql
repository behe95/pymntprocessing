CREATE TABLE Vendor (
    vendorPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    address VARCHAR(100),
    vendorId VARCHAR(50) UNIQUE
);

CREATE TABLE PaymentTransaction (
    paymentTransactionPk INT PRIMARY KEY AUTO_INCREMENT,
    fkVendor INT,
    fkCheck INT,
    fkTransactionStatus INT,        -- batched, listed, posted, paid
    fkTransactionType INT,          -- debit, credit
    transactionNumber INT UNIQUE,
    transactionDescription VARCHAR(100),
    transactionAmount DECIMAL(15,2)
);


drop table TransactionStatus;
CREATE TABLE TransactionStatus (
    transactionStatusPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    code INT UNIQUE
);

INSERT INTO TransactionStatus(name, code)
VALUES
    ('Open', 1), ('Committed', 2), ('Batched', 3), ('Listed', 4), ('Posted', 5), ('Paid', 6);

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