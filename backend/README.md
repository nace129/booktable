# BookTable API - Restaurant Reservation Backend

This Spring Boot application provides a comprehensive backend solution for a restaurant booking platform similar to OpenTable. It includes user authentication, restaurant management, reservation scheduling, reviews, and analytics.

## Key Features

- User authentication with role-based access control (Customer, RestaurantManager, Admin)
- Restaurant management and listing
- Table reservation system with confirmation emails
- Review and rating system
- Analytics dashboard for administrators
- Restaurant search with filtering by location, date, time, and party size

## Tech Stack

- **Spring Boot 3.1.5**: Backend framework
- **Spring Security**: Authentication and authorization
- **MongoDB**: Database
- **JWT**: Secure token-based authentication
- **Spring Mail**: Email notifications
- **Maven**: Dependency management

## API Endpoints

The API is organized by controllers:

### Auth Controller
- `POST /auth/register`: Register as a customer
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890"
  }
  ```
- `POST /auth/login`: Login with email and password
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```

### Restaurant Controller
- `GET /restaurants/public`: List approved restaurants
- `GET /restaurants/public/{id}`: Get restaurant details
- `POST /restaurants/public/search`: Search restaurants by criteria
  ```json
  {
    "date": "2024-03-20",
    "time": "19:00",
    "partySize": 4,
    "city": "San Francisco",
    "state": "CA",
    "cuisineType": "Italian"
  }
  ```
- `POST /restaurants`: Create new restaurant (Restaurant Manager)
- `PUT /restaurants/{id}`: Update restaurant (Restaurant Manager, Admin)
- `DELETE /restaurants/{id}`: Delete restaurant (Admin)
- `GET /restaurants/manager`: Get restaurants by manager (Restaurant Manager)
- `POST /restaurants/{id}/approve`: Approve restaurant (Admin)
- `GET /restaurants/pending`: Get pending restaurants (Admin)

### Reservation Controller
- `GET /reservations`: Get user reservations (Customer)
- `GET /reservations/{id}`: Get reservation details
- `POST /reservations`: Create reservation (Customer)
  ```json
  {
    "restaurantId": "123",
    "reservationDateTime": "2024-03-20T19:00:00",
    "partySize": 4,
    "specialRequests": "Window seat preferred"
  }
  ```
- `PUT /reservations/{id}`: Update reservation
- `DELETE /reservations/{id}`: Cancel reservation (Customer)
- `GET /reservations/restaurant/{restaurantId}`: Get restaurant reservations (Restaurant Manager)

### Review Controller
- `GET /reviews/public/restaurant/{restaurantId}`: Get restaurant reviews
- `POST /reviews`: Create review (Customer)
  ```json
  {
    "restaurantId": "123",
    "reservationId": "456",
    "rating": 5,
    "comment": "Excellent service and food!"
  }
  ```
- `PUT /reviews/{id}`: Update review (Customer)
- `DELETE /reviews/{id}`: Delete review (Customer, Admin)
- `GET /reviews/user`: Get user reviews (Customer)

### Admin Controller
- `GET /admin/analytics/reservations`: Get reservation analytics
- `GET /admin/users`: Get all users
- `POST /admin/users/{id}/role/{role}`: Add role to user
- `DELETE /admin/users/{id}/role/{role}`: Remove role from user
- `PUT /admin/users/{id}/disable`: Disable user
- `PUT /admin/users/{id}/enable`: Enable user

## Model Structure

- **User**: Basic user information with role-based permissions
- **Restaurant**: Complete restaurant details including tables and availability
- **Reservation**: Booking information with status tracking
- **Review**: Restaurant ratings and comments
- **Address**: Location information with geocoding support
- **Table**: Table information with capacity and availability

## Security Features

- JWT-based authentication
- Role-based access control
- Password encryption
- API endpoint security

## Setup & Configuration

1. Update MongoDB connection settings in `application.properties`
2. Configure email settings for reservation confirmations
3. Update JWT secret key for production environments
4. Build with Maven: `mvn clean package`
5. Run the application: `java -jar target/api-0.0.1-SNAPSHOT.jar`