## Setting up local development environment

## Step 1. Clone the repository and install tools

```
git clone https://github.com/icodebetter/icodebetter.git
cd icodebetter
yarn install # or npm install
```
Install [PostgreSQL](https://www.postgresql.org/download/) database and [Eclipse](https://www.eclipse.org/downloads/packages/) IDE on your machine.
> You can use IDE of your choice. We will demonstrate how to setup the project using Eclipse in this tutorial.

## Step 2. Setup database

#### 1. Create new database in pgAdmin

  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/5904c2b7/other/pg1.png" alt="pg" title="pgadmin" width="400">
  <br>


#### 2. Restore icodebetter database from backup file

  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/cd78e113/other/pg2.png" alt="pg" title="pgadmin" width="300">
  <br>
  
> NOTE: icb_db.backup file is in root directory

  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/cd78e113/other/pg3.png" alt="pg" title="pgadmin" width="500">
  <br>
  
  ## 3. Open the project in Eclipse
  #### 1. Import existing maven project
  
  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/6ee87112/other/pg4.png" alt="pg" title="pgadmin" width="300">
  <br>
  
  Type existing maven projects and select it.
  
  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/6ee87112/other/pg5.png" alt="pg" title="pgadmin" width="300">
  <br>
  Then click next and select icodebetter project directory.
  
  #### 2. Create application.properties file
  Create application.properties file under src/main/resources directory. And paste this code replacing placeholders with your own database name, database username and password. 
  
  ```
  spring.datasource.url=jdbc:postgresql://localhost:5432/[DATABASE_NAME]
  spring.datasource.username=[USERNAME]
  spring.datasource.password=[PASSWORD]
  spring.datasource.driver-class-name=org.postgresql.Driver
  spring.jpa.properties.hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext
  server.port = 8080
  ```
  
  
  ## Run the application

  <br>
    <img src="https://cdn.rawgit.com/icodebetter/icodebetter/ad761c4d/other/pg6.png" alt="pg" title="pgadmin" width="600">
  <br>

  
