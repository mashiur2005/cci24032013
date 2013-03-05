CCIDIST
=======

This is the code repository of the CCI Distribution Server. The server provides access to
epubs (http://idpf.org/epub/30) trough a set of RESTful web services.

Installation prerequisites
--------------------------

The distribution server is written in Java 7. The build system it Maven 3. Thus, to be able to clone, build and run
this application you need the following to be installed on your computer

 1. Git. Git must be configured with the necessary certificates to connect to your GitHub account.
 2. Java 7
 3. Maven 3
 4. A MySQL database. According to plan, the distribution service will eventually support Oracle, but currently it
    has only been tested with MySQL

Installation instructions
-------------------------

1. Create a new directory: `mkdir ccidist`
2. Change to the new directory:`cd ccidist`
3. Clone the CCIDIST repository:
4. Build the code: `mvn clean package`
5. Create the database:
6. Make sure the application can access the database.
7. Start the server: `mvn jetty:run`

