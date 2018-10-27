#include <stdlib.h>
#include <boost/thread.hpp>

#include "../include/connectionHandler.h"
#include "../include/Task.h"
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    Task readFromServer(1, connectionHandler);
    Task sendToServer(2, connectionHandler);
    boost::thread inThread(&Task::receive, &readFromServer);
    boost::thread outThread(&Task::send, &sendToServer);
    //close threads
    inThread.join();
    outThread.join();
    return 0;
}
