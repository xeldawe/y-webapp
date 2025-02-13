markdown
# Project Installation and Setup (Windows)

## 1. Install Docker
- Download and install Docker Desktop for Windows from the [official Docker website](https://www.docker.com/products/docker-desktop).
- Follow the installation instructions.
- After installation, open Docker Desktop to ensure it is running and properly set up.
- To update Docker Compose, open a terminal and run:
  ```sh
  docker-compose --version
If you need to update, follow the instructions on the official Docker documentation.

## 2. Run the Application
- Open a terminal or command prompt.
- Navigate to the directory containing your project.
- Run the run-app.bat script:

  ```sh
    run-app.bat
You will be prompted to enter an API key. For example, you can use 768Bu2k1 or any other key. This will create and start the Docker Compose setup.

## 3. Initialize Pets
- Run the init_pets.bat script to populate the database with dummy pet data:
  ```sh
  init_pets.bat
## 4. Fix Integration Project Database
- Run the fix.bat script to fix the integration project database:
  ```sh
  fix.bat
## 5. Enjoy
Your application should now be up and running. Here are the accessible endpoints and ports:

### Services and Endpoints

- Angular Application: Available on port 4200.
#
- Music App via NGINX: Available on port 8100.
- Music App Swagger UI: Available on port 8190.
#
- Postgres Database: Accessible externally on port 7000.
- MySQL Database: Accessible on port 3306.
- Redis: Available on port 6379.
#
- Java Async Servlet: Available on port 8080.
- Java Async Servlet Swagger UI: Available at 8080/swagger-ui/index.html.
#     
- Vert.x Store Endpoints: Available on port 8091.
- Vert.x Pet Endpoints: Available on port 8092.

Follow these steps to get your environment up and running.
