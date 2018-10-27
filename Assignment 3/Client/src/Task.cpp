#include <iostream>
#include "../include/Task.h"

Task::Task (int id, ConnectionHandler& connectionHandler): _id(id), connectionHandler(connectionHandler){}

//Send messages to server
void Task:: send() {
    while (true) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!connectionHandler.isConnected()|| !connectionHandler.sendLine(line))
            break;
    }
}
//Receive incoming messages from server.
void Task:: receive(){
    while (true) {
        std::string incomingMessage;
        if (!connectionHandler.getLine(incomingMessage)) {
            break;
        }
        int len=incomingMessage.length();
        incomingMessage.resize(len-1);
        std::cout << incomingMessage << std::endl;
        if (incomingMessage == "ACK signout succeeded") {
            connectionHandler.close();
	    std::cout <<"Ready to exit. Press enter" <<std:: endl;
            break;
        }
    }
}
