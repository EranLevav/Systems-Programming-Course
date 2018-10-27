
#ifndef TASK_H
#define TASK_H

#include <iostream>
#include <boost/thread.hpp>
#include "../include/connectionHandler.h"
class Task{
private:
    int _id;
    ConnectionHandler& connectionHandler;
    //boost::mutex * _mutex;
public:
    Task(int id, ConnectionHandler& connectionHandler);
            //, boost::mutex* mutex);
    void receive();
    void send();
};
#endif //TASK_H
