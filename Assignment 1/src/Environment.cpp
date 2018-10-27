using namespace std;


#include "../include/Environment.h"
#include <iostream>
#include <vector>
#include <algorithm>
#include "../include/Commands.h"
//TODO change environment.h location??


Environment::Environment(): commandsHistory(), fs(){}//TODO Check memory
void Environment::start(){
    verbose=0;
    //TEST
//    BaseCommand* testDirs= new MkdirCommand("eran/nati/roy/lev");
//    testDirs->execute(fs);
//    BaseCommand* testDirs2= new CdCommand("eran/nati/roy");
//    testDirs2->execute(fs);
    cout<< fs.getWorkingDirectory().getAbsolutePath() << ">"; //no endl
    string input;
    getline (cin, input);
    BaseCommand* command;
    string commandName;
    string args;
    while(input!="exit"){
        commandName= BaseCommand::getFirstArg(input);
        args= BaseCommand::getSecondArg(input);
        verbosePrint(input); //todo order matter
        //Todo check what to push to vector
        if(commandName=="pwd") {
            command = new PwdCommand(args);
        }
        else if(commandName=="cd"){
            command= new CdCommand(args);
        }
        else if(commandName=="ls"){
            command= new LsCommand(args);
        }
        else if(commandName=="mkdir"){
            command= new MkdirCommand(args);
        }
        else if(commandName=="mkfile"){
            command= new MkfileCommand(args);
        }
        else if(commandName=="cp"){
            command= new CpCommand(args);
        }
        else if(commandName=="mv"){
            command= new MvCommand(args);
        }
        else if(commandName=="rename"){
            command= new RenameCommand(args);
        }
        else if(commandName=="rm"){
            command= new RmCommand(args);
        }
        else if(commandName=="history"){
            command= new HistoryCommand(args, commandsHistory);
        }
        else if(commandName=="verbose"){
            command= new VerboseCommand(args);
        }
        else if(commandName=="exec"){
                command= new ExecCommand(args, commandsHistory);
        }
        else{
            command= new ErrorCommand(input); //aproval nati was input
        }
        command->execute(fs);
        addToHistory(command); //push as last command
        cout<< fs.getWorkingDirectory().getAbsolutePath() << ">"; //no endl
        getline (cin, input);

    }
}
FileSystem& Environment::getFileSystem(){
    return fs;
}// Get a reference to the file system
void Environment::addToHistory(BaseCommand *command){
    commandsHistory.push_back(command);
} // Add a new command to the history
const vector<BaseCommand*>& Environment::getHistory() const{
    return commandsHistory;
} // Return a reference to the history of commands

//Additional methods
void Environment::clearCommandsHistory() {
    //commandsHistory.clear();
    while (!commandsHistory.empty()) {
        delete commandsHistory.back();
        commandsHistory.pop_back();
    }
    commandsHistory.clear(); //TODO OVERKILL
}

void Environment::verbosePrint(string input){
    if(verbose==2 || verbose==3){   //compare strings to print command in the first time in the loop (if the first command entered is verbose 2/3)
        cout<<input<<endl;
    }
}//TODO first command verbose need to print "verbose 2/3"?

//Rule of 5
// Destructor
Environment::~Environment(){
    if ((verbose==1) | (verbose==3)){
        cout<<"Environment::~Environment()"<<endl;
    }
    clearCommandsHistory();
    //TODO check if fs automatically calls destructor
}
// Copy constructor
Environment::Environment(const Environment& other):commandsHistory(),fs(other.fs){
    //we initialize commandsHistroy with default constructor because of Weffc++.  copyHistory(other) does the actual copy process
    if ((verbose==1) | (verbose==3)){
        cout<<"Environment::Environment(const Environment& other)"<<endl;
    }

    copyHistory(other);
}
// Copy Assignment
Environment& Environment::operator=(const Environment& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"Environment& Environment::operator=(const Environment& other)"<<endl;
    }
    if (this != &other)
    {
        fs = other.fs;
        clearCommandsHistory();
        copyHistory(other);
    }
    return (*this);
}
// Move Constructor
Environment::Environment(Environment&& other): commandsHistory(move(other.commandsHistory)), fs(move(other.fs)){

    if ((verbose==1) | (verbose==3)){
        cout<<"Environment::Environment(Environment&& other)"<<endl;
    }
}

//Move Assignment
Environment& Environment::operator=(Environment&& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"Environment& Environment::operator=(Environment&& other)"<<endl;
    }
    if (this != &other){ //redundant

        clearCommandsHistory();
        fs=move(other.fs);
        commandsHistory=move(other.commandsHistory);

    }
    return (*this);
}



//help rule of 5
//copy for Copy Constructor
void Environment::copyHistory(const Environment& other){
    for(unsigned int i=0; i<other.commandsHistory.size(); i++){
        addToHistory(other.commandsHistory.at(i)->copyCommand());
    }
}