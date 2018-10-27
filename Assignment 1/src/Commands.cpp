
using namespace std;
#include "../include/Commands.h"
#include <iostream>
#include <string>
#include <algorithm>

//BaseCommand constructor
BaseCommand::BaseCommand(string args): args(args){}

string BaseCommand::getArgs() {
    return args;
}

//return string with no spaces in beginning and end of string
string BaseCommand::trimSpaces(string st){
    string str=st;
    size_t endpos = str.find_last_not_of(" \t");
    size_t startpos = str.find_first_not_of(" \t");
    if( std::string::npos != endpos )
    {
        str = str.substr( 0, endpos+1 );
        str = str.substr( startpos );
    }
    else {
        str.erase(remove(std::begin(str), std::end(str), ' '), std::end(str));
    }
    return str;
}
string BaseCommand::getStartOfPath(string path){
    size_t pos = path.find_last_of("/");
    // path is the rest of the path to check
    if(pos!=string::npos)
    {
        path = path.substr(0,pos);
        if(path==""){
            path="/";
        }
    }
    else {path="";}
    return path;
}
string BaseCommand::getEndOfPath(string path){
    size_t pos = path.find_last_of("/");
    // path is the rest of the path to check
    if(pos!=string::npos)
    {
        path = path.substr(pos+1);
    }
    return path;
}

string BaseCommand::getFirstArg(string str){
    size_t pos = str.find_first_of(" ");
    // path is the rest of the path to check
    if(pos!=string::npos) {
        str = str.substr(0, pos);
    }
    return str;
}
string BaseCommand::getSecondArg(string str){
    size_t pos = str.find_first_of(" ");
    // path is the rest of the path to check
    if(pos!=string::npos) {
        str = str.substr(pos+1);
    }
    else{
        str="";
    }
    return str;
}

//PwdCommand constructor
PwdCommand::PwdCommand (string args) : BaseCommand(args){}

void PwdCommand:: execute (FileSystem & fs){
    cout<< fs.getWorkingDirectory().getAbsolutePath() << endl;
}
string PwdCommand:: toString(){
    string output= "pwd ";
    output+= getArgs();
    return output;
}
PwdCommand* PwdCommand::copyCommand(){
    return (new PwdCommand(getArgs()));
}

//CdCommand constructor
CdCommand::CdCommand (string args) : BaseCommand(args){}

 void CdCommand:: execute(FileSystem & fs){
     if (!fs.setWorkingDirIfLegalPath(getArgs())){
         cout<< "The system cannot find the path specified" << endl;
     }
 }
string CdCommand:: toString(){
     string output= "cd ";
     output+= getArgs();
        return output;
    }
CdCommand* CdCommand::copyCommand(){
    return (new CdCommand(getArgs()));
}

LsCommand::LsCommand(string args):BaseCommand(args){}
void LsCommand::execute(FileSystem & fs){
    string noSpaceArgs= trimSpaces(getArgs());
    bool sortBySize=(noSpaceArgs.substr(0,2)=="-s");
    if(sortBySize){
        noSpaceArgs= trimSpaces(noSpaceArgs.substr(2));
    }
    //noSpaceArgs is Path
    Directory* lsDir=fs.findIfLegalPath(noSpaceArgs);
    if (lsDir!= nullptr){
            if(sortBySize){
                lsDir->sortBySize();
            }
            else{
                lsDir->sortByName();
            }
            lsDir->print();
    }
    else{
        cout<< "The system cannot find the path specified" << endl;
    }
}

string LsCommand::toString(){
    string output= "ls ";
    output+= getArgs();
    return output;
}
LsCommand* LsCommand::copyCommand(){
    return (new LsCommand(getArgs()));
}

MkdirCommand::MkdirCommand(string args):BaseCommand(args){}
void MkdirCommand::execute(FileSystem & fs){
//    string argsPath= trimSpaces(getArgs());nati aproval work without comment
//    fs.findIfLegalPath(argsPath, true);
//
     string argsPath= trimSpaces(getArgs());
     Directory* dir=fs.findIfLegalPath(argsPath, true);
    if (dir != nullptr){
        cout << "The directory already exists" << endl;
        return;
    }
}
string MkdirCommand::toString(){
    string output= "mkdir ";
    output+= getArgs();
    return output;
}
MkdirCommand* MkdirCommand::copyCommand(){
    return (new MkdirCommand(getArgs()));

}

//TODO:  ASSUMING LEGAL ARGUMENTS
MkfileCommand::MkfileCommand(string args):BaseCommand(args){}
void MkfileCommand::execute(FileSystem & fs){
    string argsNoSpace= trimSpaces(getArgs());
    int size= std::stoi(getSecondArg(argsNoSpace));
    string path= getFirstArg(argsNoSpace);
    string fileName= getEndOfPath(path);
    string filePath= getStartOfPath(path);
    Directory* fileDir = fs.findIfLegalPath(filePath);
    if (fileDir== nullptr){
        cout<< "The system cannot find the path specified" << endl;
        return;
    }
    File* file = fileDir->findFile(fileName);
    if (file != nullptr){
        cout << "File already exists" << endl;
        return;
    }

    fileDir->addFile(new File(fileName,size));

}

string MkfileCommand::toString(){
    string output= "mkfile ";
    output+= getArgs();
    return output;
}
MkfileCommand* MkfileCommand::copyCommand(){
    return (new MkfileCommand(getArgs()));
}

///CP////
CpCommand::CpCommand(string args): BaseCommand(args){};
void CpCommand::execute(FileSystem & fs){

    string src= getFirstArg(trimSpaces(getArgs()));
    string dest= getSecondArg(trimSpaces(getArgs()));
    Directory* destDir=fs.findIfLegalPath(dest);
    if(destDir== nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    //checking source
    string toCopyName= getEndOfPath(src);
    string filePath= getStartOfPath(src);
    Directory* srcDir= fs.findIfLegalPath(filePath);
    if(srcDir==nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    BaseFile* toCopy= srcDir->find(toCopyName);
    if(toCopy==nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    if(destDir->isChild(toCopyName)){   // if a file/ dir with the same name exists in destination's children, stop the action without printing anything
        return;
    }
    //file or dir toCopy found
    //check isDir:
    if(toCopy->isDir()){
        destDir->addFile(new Directory(*(Directory*)toCopy));   // check if casting works
    }
    else{   //is a file
        File* file=(File*)toCopy;
        destDir->addFile(new File(file->getName(),file->getSize()));
    }
}
string CpCommand::toString(){
    string output= "cp ";
    output+= getArgs();
    return output;
}
CpCommand* CpCommand::copyCommand(){
    return (new CpCommand(getArgs()));
}

MvCommand::MvCommand(string args):BaseCommand(args){}
void MvCommand::execute(FileSystem & fs) {
    Directory* workingDir=&(fs.getWorkingDirectory());
    Directory* rootDir=&(fs.getRootDirectory());
    string src = getFirstArg(trimSpaces(getArgs()));
    string dest = getSecondArg(trimSpaces(getArgs()));
    Directory *destDir = fs.findIfLegalPath(dest);

    //checking destination
    if (destDir == nullptr) {
        cout << "No such file or directory" << endl;
        return;
    }
    //checking source
    string toMoveName = getEndOfPath(src);
    string filePath = getStartOfPath(src);
    Directory *srcDir = fs.findIfLegalPath(filePath);
    if (srcDir == nullptr) {
        cout << "No such file or directory" << endl;
        return;
    }
    BaseFile *toMove = srcDir->find(toMoveName);
    if (toMove == nullptr) {
        if(srcDir==rootDir && toMoveName==""){
            toMove=rootDir;
        }
        else{
            cout << "No such file or directory" << endl;
            return;
        }
    }
    //file/dir to move found
    //check isDir:
    if (toMove->isDir()) {
        Directory* dirToMove= (Directory *)toMove;
        if ( dirToMove == workingDir || dirToMove == rootDir || workingDir->isAncestor(dirToMove)){

            cout << "Can't move directory" << endl;
            return;
        }
        destDir->addFile(toMove);   // we added if needed , also addfile set perant
    }
    else {   //is a file
        File *file = (File *) toMove;
        destDir->addFile(file);
      }
    srcDir->removeFromChildren(toMove);
}

string MvCommand::toString() {
    string output = "mv ";
    output += getArgs();
    return output;
}

MvCommand* MvCommand::copyCommand(){
    return (new MvCommand(getArgs()));
}

///RENAME

RenameCommand::RenameCommand(string args): BaseCommand(args){};

void RenameCommand::execute(FileSystem & fs){
    Directory* workingDir= &(fs.getWorkingDirectory());
    Directory* rootDir= &(fs.getRootDirectory());
    string path= trimSpaces(getArgs());
    string src= getFirstArg(path);


    string newName= getSecondArg(path);
    //checking source
    string oldName= getEndOfPath(src);
    string filePath= getStartOfPath(src);
    Directory* srcDir= fs.findIfLegalPath(filePath);
    if(srcDir==nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    //If trying to rename root, just return; (and print error if it's also the working dir)
    if(srcDir==rootDir && oldName==""){
        if(rootDir==workingDir){
            cout << "Can't rename the working directory" << endl;
        }
    }
    BaseFile* toChange= srcDir->find(oldName);
    if(toChange==nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    //file/dir to copy found
    //check isDir:
    if(toChange->isDir() && toChange==workingDir){
        cout << "Can't rename the working directory" << endl;
        return;
    }
    //check if there is a child with newName, if there isn't- execute rename
    if(!srcDir->isChild(newName)) {
        toChange->setName(newName);
    }
}
string RenameCommand::toString(){
    string output= "rename ";
    output+= getArgs();
    return output;
}
RenameCommand* RenameCommand::copyCommand(){
    return (new RenameCommand(getArgs()));
}

RmCommand::RmCommand(string args):BaseCommand(args){}

void RmCommand::execute(FileSystem & fs){
    string path= trimSpaces(getArgs());
    string toRemoveName= getEndOfPath(path);
    string toRemovePath= getStartOfPath(path);
    Directory* parentDir= fs.findIfLegalPath(toRemovePath);

    if(toRemovePath =="/") { //plaster aproval
        cout << "Can't remove directory" << endl;
        return;
    }

        if(parentDir==nullptr){
        cout << "No such file or directory" <<endl;
        return;
    }

    BaseFile* toRemove= parentDir->find(toRemoveName);
    if(toRemove==nullptr){
        cout <<"No such file or directory" <<endl;
        return;
    }
    //file/dir to copy found
    //check isDir:
    Directory* workingDir= &(fs.getWorkingDirectory());

    if(toRemove->isDir() && (   (Directory*)toRemove==workingDir  || (Directory*)toRemove==&(fs.getRootDirectory()) || workingDir->isAncestor((Directory*)toRemove) ) ){
        cout << "Can't remove directory" << endl;
        return;
    }
    parentDir->removeFile(toRemove);
}
string RmCommand::toString(){
    string output= "rm ";
    output+= getArgs();
    return output;
}
RmCommand* RmCommand::copyCommand(){
    return (new RmCommand(getArgs()));
}

HistoryCommand::HistoryCommand(string args, const vector<BaseCommand *> & history):BaseCommand(args), history(history){}
void HistoryCommand::execute(FileSystem & fs){
    for(unsigned int i=0; i< history.size(); i++){
        cout<< i<< '\t' << history.at(i)->toString() <<endl;
    }
}
string HistoryCommand::toString(){
    string output= "history ";
    output+= getArgs();
    return output;
}
HistoryCommand* HistoryCommand::copyCommand(){
    return (new HistoryCommand(getArgs(), history));
}

VerboseCommand::VerboseCommand(string args):BaseCommand(args){}
void VerboseCommand::execute(FileSystem & fs) {

    if (getArgs()=="1") {
        verbose = 1;
    }
    else if (getArgs()=="2") {
        verbose = 2;
    }
    else if (getArgs()=="3") {
        verbose = 3;
    }
    else if (getArgs()=="0") {
        verbose = 0;
    }
    else {
        cout << "Wrong verbose input" << endl;
    }
}
string VerboseCommand::toString(){
    string output= "verbose ";
    output+= getArgs();
    return output;
}

VerboseCommand* VerboseCommand::copyCommand(){
    return (new VerboseCommand(getArgs()));
}

ExecCommand::ExecCommand(string args, const vector<BaseCommand *> & history):BaseCommand(args), history(history){}
void ExecCommand::execute(FileSystem & fs){
    string argsNoSpace= trimSpaces(getArgs());
    unsigned int index= std::stoi(argsNoSpace);
    if(index >= history.size()){
        cout <<"Command not found" << endl;
        return;
    }
    history.at(index)->execute(fs);
}
string ExecCommand::toString(){
    string output= "exec ";
    output+= getArgs();
    return output;
}

ExecCommand* ExecCommand::copyCommand(){
  return (new ExecCommand(getArgs(), history));
 }

ErrorCommand::ErrorCommand(string args):BaseCommand(args){}
void ErrorCommand::execute(FileSystem & fs){
    cout<< getFirstArg(getArgs()) << ": Unknown command" <<endl;
}

string ErrorCommand::toString(){
    return getArgs();
}
ErrorCommand* ErrorCommand::copyCommand(){
    return (new ErrorCommand(getArgs()));
}