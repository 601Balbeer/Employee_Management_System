CREATE DATABASE PayrollDB;
USE PayrollDB;

CREATE TABLE Employees (
    empID INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    basicSalary DOUBLE NOT NULL,
    allowance DOUBLE DEFAULT 0,
    deduction DOUBLE DEFAULT 0
);
