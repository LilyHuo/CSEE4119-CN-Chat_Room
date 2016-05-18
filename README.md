# CSEE4119-CN-Chat_Room

DESCRIPTRION:

There are totally 4 java files in this project, namely Server, Client, User and ValidUser.

Server:  
Listening on a specific port, accept a connection when a request comes and create a User object for each connection;

Client:
Initiate the connection, fulfill the message exchange task from the client side;

User:
Record the necessary information for each connection/client, serve the command and authentication service for each client, all user objects maintain one copy of current user and blacklist.

ValidUser:
Provide the encryption for each user, fulfill the password validation procedure

Note: the  BLOCK_OUT and TIME_OUT variables are set in the User.java file.

ENVIRONMENT:
This project is developed on Java 1.6

HOW TO RUN:
Begin (to compile and interpret the code): 
$ make

For the Server side:
$ java Server <port_number>

For the Client side:
$ java Client <ip_address> <port_number>

Note :
1. You might need to reopen a new window or type “Control + c” to re-get the control of the Terminal window

SAMPLE COMMANDS:
After initiate the Server side, it will immediately display a message “the server is running”. 
After initiate the Client side, it will immediately display a message “Username : ”, which requires you to enter the username, and then a message “Password : ”, which requires you to enter the password. If the username and password is matched, then it will display “Welcome to the simple chat server!”. Then you can enter the following commands:

•who:  return all the usernames who currently log in
•last <number>:  e.g. last 5, display the usernames who logged in within last 5 minutes
•broadcast <message>: e.g. broadcast hi, which will send all users (include the sender itself) a “hi” message
•send <user> <message>: e.g. send columbia hi, which will send the user named “columbia” a “hi” message 
•send (<user>…<user>) <message>: e.g. send (columbia, seas) hello, which will send the users named “columbia” and “seas” a “hello” message
•logout: logout the user itself and close this connection


