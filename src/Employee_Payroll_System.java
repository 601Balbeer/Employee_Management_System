import java.sql.*;
import java.util.Scanner;

public class Employee_Payroll_System {


        // === DB Config ===
        private static final String URL = "jdbc:mysql://localhost:3306/PayrollDB";
        private static final String USER = "root";  // your MySQL username
        private static final String PASS = "Balbeer@1333";  // your MySQL password

        private static final Scanner sc = new Scanner(System.in);

        public static void main(String[] args) {
            int choice;
            do {
                System.out.println("\n----- Employee Payroll System -----");
                System.out.println("1. Add Employee");
                System.out.println("2. View Employees");
                System.out.println("3. Search Employee");
                System.out.println("4. Update Salary");
                System.out.println("5. Calculate Net Salary");
                System.out.println("6. Delete Employee");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 : addEmployee();break;
                    case 2 : viewEmployees();break;
                    case 3 : searchEmployee();break;
                    case 4 : updateSalary();break;
                    case 5 : calculateNetSalary();break;
                    case 6 : deleteEmployee();break;
                    case 0 : System.out.println("Exiting... Thank you!");break;
                    default : System.out.println("Invalid choice!");
                }
            } while (choice != 0);
        }

        // === Add Employee ===
        private static void addEmployee() {
            System.out.print("Enter Employee ID: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter Department: ");
            String dept = sc.nextLine().trim();
            System.out.print("Enter Basic Salary: ");
            double basic = sc.nextDouble();

            // Automatically calculate allowance and deduction
            double allowance = basic * 0.10;  // 10% of basic
            double deduction = basic * 0.05;  // 5% of basic

            String sql = "INSERT INTO Employees (empID, name, department, basicSalary, allowance, deduction) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, id);
                pst.setString(2, name);
                pst.setString(3, dept);
                pst.setDouble(4, basic);
                pst.setDouble(5, allowance);
                pst.setDouble(6, deduction);

                int rows = pst.executeUpdate();
                System.out.println(rows > 0 ? "✅ Employee added successfully!" : "❌ Insert failed.");
            } catch (SQLIntegrityConstraintViolationException dup) {
                System.out.println("⚠️ Employee ID already exists.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // === View Employees ===
        private static void viewEmployees() {
            String sql = "SELECT * FROM Employees ORDER BY empID";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                System.out.println("\n--- Employee List ---");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("ID: %d | Name: %s | Dept: %s | Basic: %.2f | Allowance: %.2f | Deduction: %.2f%n",
                            rs.getInt("empID"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getDouble("basicSalary"),
                            rs.getDouble("allowance"),
                            rs.getDouble("deduction"));
                }
                if (!any) System.out.println("(No records found)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // === Search Employee ===
        private static void searchEmployee() {
            System.out.print("Enter Employee ID to search: ");
            int id = sc.nextInt();

            String sql = "SELECT * FROM Employees WHERE empID = ?";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        System.out.printf("ID: %d | Name: %s | Dept: %s | Basic: %.2f | Allowance: %.2f | Deduction: %.2f%n",
                                rs.getInt("empID"),
                                rs.getString("name"),
                                rs.getString("department"),
                                rs.getDouble("basicSalary"),
                                rs.getDouble("allowance"),
                                rs.getDouble("deduction"));
                    } else {
                        System.out.println("⚠️ Employee not found.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // === Update Salary ===
        private static void updateSalary() {
            System.out.print("Enter Employee ID to update: ");
            int id = sc.nextInt();
            System.out.print("Enter new Basic Salary: ");
            double basic = sc.nextDouble();

            // Recalculate allowance & deduction automatically
            double allowance = basic * 0.10;
            double deduction = basic * 0.05;

            String sql = "UPDATE Employees SET basicSalary=?, allowance=?, deduction=? WHERE empID=?";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setDouble(1, basic);
                pst.setDouble(2, allowance);
                pst.setDouble(3, deduction);
                pst.setInt(4, id);

                int rows = pst.executeUpdate();
                System.out.println(rows > 0 ? "✅ Salary updated successfully." : "⚠️ Employee not found.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // === Calculate Net Salary ===
        private static void calculateNetSalary() {
            System.out.print("Enter Employee ID: ");
            int id = sc.nextInt();

            String sql = "SELECT basicSalary, allowance, deduction FROM Employees WHERE empID=?";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        double basic = rs.getDouble("basicSalary");
                        double allowance = rs.getDouble("allowance");
                        double deduction = rs.getDouble("deduction");
                        double net = basic + allowance - deduction;

                        System.out.printf("Net Salary for Employee ID %d: ₹%.2f%n", id, net);
                    } else {
                        System.out.println("⚠️ Employee not found.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // === Delete Employee ===
        private static void deleteEmployee() {
            System.out.print("Enter Employee ID to delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM Employees WHERE empID=?";
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, id);
                int rows = pst.executeUpdate();
                System.out.println(rows > 0 ? "✅ Employee deleted successfully." : "⚠️ Employee not found.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


