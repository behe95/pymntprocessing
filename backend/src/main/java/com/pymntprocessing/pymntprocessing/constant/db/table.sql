CREATE TABLE Vendor (
    vendorPk INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    address VARCHAR(100),
    vendorId VARCHAR(50) UNIQUE
);