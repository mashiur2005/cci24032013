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

1. Change directory to wherever you store your git projects
   eg:`cd ~/gitrepos`
   You do  ot need to create the root directory of the as this will be create when the project is cloned in the next
   step.
2. Clone the CCIDIST repository

   `git clone https://github.com/Cefalo/ccidist.git`

3. Change directory to the project root

   `cd ccidist`

4. Build the code

   `mvn clean package`

5. Create the database:

6. Make sure the application can access the database.

7. Start the server: `mvn jetty:run`

