package com.cognixia.jump.jdbc.ems_extension.team2;

import java.util.List;
import java.util.Scanner;

import com.cognixia.jump.jdbc.ems_extension.team2.concrete.AddressDAOClass;
import com.cognixia.jump.jdbc.ems_extension.team2.concrete.CompanyDAOClass;
import com.cognixia.jump.jdbc.ems_extension.team2.concrete.DepartmentDAOClass;
import com.cognixia.jump.jdbc.ems_extension.team2.concrete.EmployeeDAOClass;
import com.cognixia.jump.jdbc.ems_extension.team2.model.Address;
import com.cognixia.jump.jdbc.ems_extension.team2.model.Company;
import com.cognixia.jump.jdbc.ems_extension.team2.model.Department;
import com.cognixia.jump.jdbc.ems_extension.team2.model.Employee;

public class Runner {
	
	//runner main
	public static void main(String[] args) {
		//this creates the needed objects for everything to work
		Scanner scanner = new Scanner(System.in);
		boolean keepProgramRunning = true;
		CompanyDAOClass companyDAO = new CompanyDAOClass();
		DepartmentDAOClass departmentDAO = new DepartmentDAOClass();
		EmployeeDAOClass employeeDAO = new EmployeeDAOClass();
		int companyId;
		
		//first method that polls the database for any existing companies.
		companyId = startUp(scanner, companyDAO);
		
		//holds the main logic
		userLoop(scanner, companyDAO, departmentDAO, employeeDAO, companyId, keepProgramRunning);
		
		//closing the scanner for security and safety.
		System.out.println("... Program Terminated");
		scanner.close();
	}



	// startup, main user loop, and shutdown methods
	private static int startUp(Scanner scanner, CompanyDAOClass companyDAO) {
		System.out.println("...  Loading database connection");
		List<Company> companies = companyDAO.getAllCompanies();
		
		//check database for any existing companies
		if (companies == null || companies.isEmpty()) {
			//if no companies, creates one with user
			System.out.println("... No created companies were detected");
			System.out.println("... Creating company from user input:");
			
			//reuses method for creating company
			Company tempCompany = createCompany(scanner, companyDAO);
			companyDAO.createCompany(tempCompany);
			return tempCompany.getId();
			
		//if a company exists, ask user what company they'd like to use
		} else {
			int idCounter = 1;
			System.out.println("... Found " + companies.size() + " Companies: ");
			for (Company company : companies ) {
				System.out.printf("ID: %-2d - Name: %s%n", company.getId(), company.getName());
				if (idCounter < company.getId()) idCounter = company.getId();
			}
			idCounter++;
			System.out.printf("ID: %-2d - Name: %s%n", idCounter, "[Create a New Company]");
			System.out.println("\nID of company you'd like to use: ");
			int userChoice = Integer.parseInt(scanner.nextLine());
			
			//checks if user would like to make new company:
			if (userChoice == idCounter) {
				System.out.println("... Creating company from user input:");
				Company tempCompany = createCompany(scanner, companyDAO);
				companyDAO.createCompany(tempCompany);
				return tempCompany.getId();
			} else {
				return userChoice;
			}
		}
	}
	
	
	private static Company createCompany(Scanner scanner, CompanyDAOClass companyDAO) {
		//collects update info
		System.out.println("Please fill out the following fields to create a Company:");
		System.out.println("--What is the Company's name?");
		String name = scanner.nextLine();
		System.out.println("--What is the Company's budget?");
		int budget = Integer.parseInt(scanner.nextLine());
		Company tempCompany = new Company(name, budget);
		int tempID = companyDAO.createCompany(tempCompany);
		
		//attempts update
		if (tempID != 0) {
			System.out.println("Company created successfully!");
			return tempCompany;
		} else {
			System.out.print("Fatal Error.");
			System.exit(0);
		}
		
		return null;
	}

	
	private static void userLoop(Scanner scanner, CompanyDAOClass companyDAO, DepartmentDAOClass departmentDAO,
			EmployeeDAOClass employeeDAO, int companyId, boolean keepProgramRunning) {
		//while loop that keeps running till the user tells it to stop.
		do {
			System.out.println("\n= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
			System.out.println("Which option would you like to choose?");
			System.out.println("1: Employee\n" + "2: Department\n" + "3: Company\n" + "4: Address\n"
								+ "5: Terminate Program");
			int userInput = Integer.parseInt(scanner.nextLine());
			System.out.println();
			
			switch (userInput) {
				//===Employee Options:
				case 1:
					System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
					employeeOptions(scanner, employeeDAO, companyId);
					break;
				
				//===Department Options:
				case 2:
					System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
					departmentOptions(scanner, departmentDAO, companyId);
					break;
					
				//===Company Options:
				case 3:
					System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
					companyOptions(scanner, companyDAO, companyId);
					break;
					
				//===Address:
				case 4:
					System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
					addressOptions(scanner);
					break;
						
				//===Quitting:
				case 5:
					System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
					System.out.println("Terminating Program...\n");
					keepProgramRunning = false;
					break;
						
				//===Default Option:
				default:
					System.out.println("Unrecognized input, restarting options...\n");
					break;
				}
			} while (keepProgramRunning);
		}

	
	
	// user choices methods
	private static void addressOptions(Scanner scanner) {
		//temp variables:
		AddressDAOClass addressDAO = new AddressDAOClass();
		int addressId;
		String street, city, state, zip;
		Address tempAddress;
		
		
		System.out.println("Select from the following Address options:");
		System.out.println("1: List all Addresses\n" + "2: Update an Address\n" + 
							"3: Create an Address\n" + "4: Remove a Address");
		int userInput = Integer.parseInt(scanner.nextLine());
		System.out.println();
		
		switch(userInput) {
			//List Addresses
			case 1:
				List<Address> addresses = addressDAO.getAllAddresses();
				for (Address address : addresses) {
					String fullAddress = address.getStreetAddr() + ", " + address.getCity() + ", " + address.getState() + " " + address.getZipCode();
					System.out.printf("ID: %-2d - %s%n", address.getId(), fullAddress);
				}
				break;
				
			//Update Address
			case 2:
				//collects update info
				System.out.println("Please fill out the following fields to update an Address:");
				System.out.println("--What is the Address Id?");
				addressId = Integer.parseInt(scanner.nextLine());
				tempAddress = addressDAO.getAddress(addressId);
				System.out.println("--What is the Street Address (no city, state, or zip)?");
				tempAddress.setStreetAddr(scanner.nextLine());
				System.out.println("--What is the City?");
				tempAddress.setCity(scanner.nextLine());
				System.out.println("--What is the State?");
				tempAddress.setState(scanner.nextLine());
				System.out.println("--What is the Zip Code?");
				tempAddress.setZipCode(scanner.nextLine());
				if (addressDAO.updateAddress(tempAddress)) {
					System.out.println("Address successfully updated!");
				} else {
					System.out.println("Failure. :<");
				}
				break;
				
			//Create Address
			case 3:
				//collects update info
				System.out.println("Please fill out the following fields to create an Address:");
				System.out.println("--What is the Street Address (no city, state, or zip)?");
				street = scanner.nextLine();
				System.out.println("--What is the City?");
				city = scanner.nextLine();
				System.out.println("--What is the State?");
				state = scanner.nextLine();
				System.out.println("--What is the Zip Code?");
				zip = scanner.nextLine();
				tempAddress = new Address(street, city, state, zip);
				int tempAddressId = addressDAO.createAddress(tempAddress);
				if (tempAddressId != 0) {
					System.out.println("Address successfully added! ID: " + tempAddressId);
				} else {
					System.out.println("Failure. :<");
				}
				break;
				
			//Remove Address
			case 4:
				System.out.println("Please fill out the following fields to remove an Address:");
				System.out.println("--What is the Address ID?");
				addressId = Integer.parseInt(scanner.nextLine());
				
				if (addressDAO.deleteAddress(addressId)) {
					System.out.println("Successfully deleted address!");
				} else {
					System.out.println("Failure. :<");
				}
				break;
				
			//default
			default:
				System.out.println("The input wasn't recognized.");
				break;
		}
		
	}

	private static void employeeOptions(Scanner scanner, EmployeeDAOClass employeeDAO, int companyId) {
		//temp variables:
		Employee tempEmployee = null;
		
		
		System.out.println("Select from the following Employee options:");
		System.out.println("1: List ALL current Employees\n" + "2: List an Employee's Full Info\n" + "3: Update a Current Employee's Info\n" + 
							"4: Create a New Employee\n" + "5: Remove a Current Employee");
		int userInput = Integer.parseInt(scanner.nextLine());
		int employeeId, departmentId;
		System.out.println();
		
		switch(userInput) {
		
		
			//gets info on an employee
			case 1:
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("Printing Employess:");
				for (Employee employee : employeeDAO.getAllEmployeesOfCompany(companyId)) {
					String employeeName = employee.getFirstName() + " " + employee.getLastName();
					System.out.printf("ID: %-2d, Name: %-30s%n", employee.getId(), employeeName);
				}
				break;
				
				
			//gets info on an employee
			case 2:
				//asks for employee ID and department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What is employee's ID?");
				employeeId = Integer.parseInt(scanner.nextLine());
				
				//prints out found info
				tempEmployee = employeeDAO.getEmployee(employeeId);
				
				if (tempEmployee != null) {
					System.out.println("Employee Found, printing info:\n"
							+ tempEmployee.toString());
				} else {
					System.out.println("Employee not found.");
				}
				break;
			
			
			//updates an employee's info
			case 3:
				//asks for employee ID and department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What is the employee's ID?");
				employeeId = Integer.parseInt(scanner.nextLine());
				tempEmployee = employeeDAO.getEmployee(employeeId);
				
				//prints out found info then asks for needed info
				if (tempEmployee != null) {
					//prints current
					System.out.println("Employee Found, printing current info:\n"
							+ tempEmployee.toString());
					
					//collects replacement info
					System.out.println("Please fill out the following fields to update the Employee:");
					System.out.println("--What is the employee's first name?");
					tempEmployee.setFirstName(scanner.nextLine());
					System.out.println("--What is the employee's last name?");
					tempEmployee.setLastName(scanner.nextLine());
					System.out.println("--What is the employee's salary?");
					tempEmployee.setSalary(Integer.parseInt(scanner.nextLine()));
					System.out.println("--What is the employee's title?");
					tempEmployee.setJobTitle(scanner.nextLine());
					System.out.println("--What is the employee's phone number?");
					tempEmployee.setPhoneNumber(scanner.nextLine());
					System.out.println("--What is the department ID that the employee belongs to?");
					tempEmployee.setDepartmentId(Integer.parseInt(scanner.nextLine()));
					
					//attempts update
					employeeDAO.updateEmployee(tempEmployee);
					System.out.println("Employee updated successfully!");
				} else {
					System.out.println("Employee not found.");
				}
				break;
			
			
			//adds an employee
			case 4:
				//ask for inputs on everything.
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("Creating an Employee...");
				System.out.println("--What is the employee's first name?");
				String firstName = scanner.nextLine();
				System.out.println("--What is the employee's last name?");
				String lastName = scanner.nextLine();
				System.out.println("--What is the employee's salary?");
				int salary = Integer.parseInt(scanner.nextLine());
				System.out.println("--What is the employee's title?");
				String title = scanner.nextLine();
				System.out.println("--What is the employee's phone number?");
				String phoneNumber = scanner.nextLine();
				System.out.println("--What is the department ID that the employee belongs to?");
				departmentId = Integer.parseInt(scanner.nextLine());
				System.out.println("--What is thier address ID?");
				int addressId = Integer.parseInt(scanner.nextLine());
				
				//gets newly generated ID if the employee was successfully added
				tempEmployee = new Employee(departmentId, firstName, lastName, salary, title, phoneNumber, addressId);
				int newID = employeeDAO.createEmployee(tempEmployee);
				
				
				//if-else statement on the received int.
				if (newID == 0) {
					System.out.println("Unable to create an employee in that department.");
				} else {
					System.out.println("Employee created, their ID is: " + newID);
				}
				break;
			
			
			//removing an employee
			case 5:
				//asks for employee ID and department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What is the employee's ID?");
				employeeId = Integer.parseInt(scanner.nextLine());
				tempEmployee = employeeDAO.getEmployee(employeeId);

				//prints out the results of removal process
				if (employeeDAO.deleteEmployee(tempEmployee)) {
					System.out.println("Employee removed.");
				} else {
					System.out.println("There was an issue removing the employee.");
				}
				break;
			
			
			//handling a bad input
			default:
				System.out.println("The input wasn't recognized.");
				break;
		}
	}
	
	private static void departmentOptions(Scanner scanner, DepartmentDAOClass departmentDAO, int companyId) {
		//temp variables:
		Department tempDepartment = null;
		
		
		System.out.println("Select from the following Department options:");
		System.out.println("1: List all Departments of Company\n" + "2: List a Department's Info & Assigned Employees\n"
							+ "3: Update a Department\n" +  "4: Create a Department\n"
							+ "5: Remove a Department");
		int userInput = Integer.parseInt(scanner.nextLine());
		int departmentId;
		System.out.println();
		
		switch(userInput) {
		
		
			//gets all departments
			case 1:
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("Printing Departments:");
				for (Department department : departmentDAO.getAllDepartmentsOfCompany(companyId)) {
					System.out.printf("ID: %-2d, Name: %-30s%n", department.getId(), department.getName());
				}
				break;
		
		
			//gets info on a department, and lists its currently assigned employees
			case 2:
				//asks for employee ID and department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What the Department's ID?");
				departmentId = Integer.parseInt(scanner.nextLine());
				
				//prints out found info
				tempDepartment = departmentDAO.getDepartment(departmentId);
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("... Department Info:");
				System.out.println(tempDepartment.toString());
				System.out.println("... Employees:");
				for (Employee employee : departmentDAO.getDepartmentEmployees(departmentId)) {
					String employeeName = employee.getFirstName() + " " + employee.getLastName();
					System.out.printf("ID: %-2d, Name: %-30s%n", employee.getId(), employeeName);
				}
				break;
			
			
			//updates an Department's info
			case 3:
				//asks for department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What is the Department's ID?");
				departmentId = Integer.parseInt(scanner.nextLine());
				tempDepartment = departmentDAO.getDepartment(departmentId);
				
				//prints out found info, then asks for needed info
				if (tempDepartment != null) {
					System.out.println("Department Found, printing current info:\n"
							+ tempDepartment.listInfo());
					
					//collecting update info
					System.out.println("Please fill out the following fields to update the Employee:");
					System.out.println("--What is the Department's name?");
					tempDepartment.setName(scanner.nextLine());
					System.out.println("--What is the Department's phone number?");
					tempDepartment.setPhoneNumber(scanner.nextLine());
					System.out.println("--What is the Department's budget?");
					tempDepartment.setBudget(Integer.parseInt(scanner.nextLine()));
					
					//attempts update
					departmentDAO.updateDepartment(tempDepartment);
					System.out.println("Department info updated successfully!");
				} else {
					System.out.println("Department not found.");
				}
				break;
			
			
			//creates a Department
			case 4:
				//ask for inputs on everything.
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("Creating a Department...");
				System.out.println("--What is the Department's name?");
				String name = scanner.nextLine();
				System.out.println("--What is the Department's phone number?");
				String phoneNumber = scanner.nextLine();
				System.out.println("--What is the Department's budget?");
				int budget = Integer.parseInt(scanner.nextLine());
				
				//gets newly generated ID if the employee was successfully added
				int newId = departmentDAO.createDepartment(new Department(name, phoneNumber, budget, companyId));
				
				//if-else statement on the received int.
				if (newId == 0) {
					System.out.println("Unable to create that Department.");
				} else {
					System.out.println("Department created, its ID is: " + newId);
				}
				break;
			
			
			//removes a Department
			case 5:
				//asks for employee ID and department ID
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("What is the Department's ID?");
				tempDepartment = departmentDAO.getDepartment(Integer.parseInt(scanner.nextLine()));

				//prints out the results of removal process
				if (departmentDAO.deleteDepartment(tempDepartment)) {
					System.out.println("Department removed.");
				} else {
					System.out.println("There was an issue removing the Department.");
				}
				break;
			
			
			//handling a bad input
			default:
				System.out.println("The input wasn't recognized.");
				break;
		}
	}
	
	private static void companyOptions(Scanner scanner, CompanyDAOClass companyDAO, int companyId) {
		//temp variables:
		Company tempCompany = companyDAO.getCompany(companyId);
		
		
		System.out.println("Select from the following Company options:");
		System.out.println("1: List the Company's Info\n"
							+ "2: Update the Company's Info\n" + "3: Delete ALL data associated with Company");
		int userInput = Integer.parseInt(scanner.nextLine());
		System.out.println();
		
		switch(userInput) {
			//gets info on the company, and lists its currently active departments
			case 1:
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				//prints out found info
				System.out.println("Printing info:\n" + tempCompany.listInfo());
				break;
			
			
			//updates an Company's info
			case 2:
				//asks for fields to be updated
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				
				//prints out found info
				if (tempCompany != null) {
					//prints out current info
					System.out.println("Printing current info:\n"
							+ tempCompany.listInfo());
					
					//collects update info
					System.out.println("Please fill out the following fields to update the Employee:");
					System.out.println("--What is the Company's name?");
					tempCompany.setName(scanner.nextLine());
					System.out.println("--What is the Company's budget?");
					tempCompany.setBudget(Integer.parseInt(scanner.nextLine()));
					
					
					//attempts update
					companyDAO.updateCompany(tempCompany);
					System.out.println("Company info updated successfully!");
				} else {
					System.out.println("Company not found.");
				}
				break;
			
			
			//resets the company data
			case 3:
				//asks confirmation before doing it.
				System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("Are you sure you want to detele all the entered company data? [y/n]");
				String answer = scanner.next().toLowerCase();

				//prints out the results of removal process
				if (answer.equals("y")) {
					System.out.println("Company deleted.");
					companyDAO.deleteCompany(tempCompany);
					System.out.println("Exiting Program.");
					System.exit(0);
				} else {
					System.out.println("There was an issue deleting the company.");
				}
				break;
			
			
			//handling a bad input
			default:
				System.out.println("The input wasn't recognized.");
				break;
		}
	}
}
