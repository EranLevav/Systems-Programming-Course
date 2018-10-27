#include "../include/FileSystem.h"
//#include "../include/Files.h"
//#include "../include/Environment.h"
//#include <typeinfo>
//#include <cstddef> // for find first of string
#include <iostream>

FileSystem::FileSystem():rootDirectory(new Directory("/", nullptr)), workingDirectory(rootDirectory){} //rootDir move constructor


void FileSystem::setWorkingDirectory(Directory *newWorkingDirectory)
{
    workingDirectory= newWorkingDirectory;
}

Directory& FileSystem::getRootDirectory() const
{
    return *rootDirectory;
}
Directory& FileSystem::getWorkingDirectory() const
{
    return *workingDirectory;
}
bool FileSystem::setWorkingDirIfLegalPath(string path){
    Directory* toChange=findIfLegalPath(path);
    if(toChange!= nullptr){
        setWorkingDirectory(toChange);
    }
    return (toChange!=nullptr);
}
bool FileSystem::isLegalPath(string path) {
    return (findIfLegalPath(path)!=nullptr);
}

Directory* FileSystem::findIfLegalPath(string path, bool mkdir) //boolean mkdir will be a default of false (see declaration). if mkdir==true path will be created

{
    Directory* originalWorkingDir= &getWorkingDirectory();
    if(path==""){
        return originalWorkingDir;
    }
    if(path=="/"){
        return  &getRootDirectory();
    }
    char c = path.at(0);
    bool isAbsolutePath= (c=='/');
    if(isAbsolutePath){
        path=path.substr(1);
        setWorkingDirectory(rootDirectory);
    }
    //from here we only work with relative path (if original was absolute path it will work from the root as working directory)
    Directory* tempDir = workingDirectory;
    size_t pos = path.find_first_of("/");
    bool isLegal= true;//(pos!=string::npos); //changed
    string firstDir; //the first dir to check
    // path is the rest of the path to check
    while (isLegal && (pos!=string::npos))
    {
        firstDir = path.substr(0, pos);
        path = path.substr(pos+1);
        pos = path.find_first_of("/");
        if(firstDir==".."){
            tempDir=tempDir->getParent();
        }
        else {
            Directory* dirToAdd= tempDir; //TODO check memory leak?? eran check
            tempDir = tempDir->findDir(firstDir);
            if(mkdir && tempDir==nullptr){
                if (dirToAdd->isChild(firstDir)) {
                    return rootDirectory;
                }
                dirToAdd->addFile(new Directory(firstDir,dirToAdd));
                tempDir=dirToAdd->findDir(firstDir);
            }
        }
        isLegal=(tempDir!=nullptr); //was && pos!=0

    }
    //if isLegalPath do far, check the last directory in Path (no "/" left in path)
    if(isLegal){
        firstDir = path;
        if(firstDir==".."){
            tempDir=tempDir->getParent();
        }
        else{
            Directory* dirToAdd= tempDir;
            tempDir = tempDir->findDir(firstDir);
            if(mkdir && tempDir==nullptr ){
                if (dirToAdd->isChild(firstDir)) {
                    return rootDirectory;

                }
             dirToAdd->addFile(new Directory(firstDir,dirToAdd));
            }
        }

    }
    if(isAbsolutePath){
        setWorkingDirectory(originalWorkingDir);
    }
    return tempDir;
}

// Destructor
FileSystem:: ~FileSystem(){
    if ((verbose==1) | (verbose==3)){
        cout<<"FileSystem:: ~FileSystem()"<<endl;

    }
    workingDirectory= nullptr;
    delete rootDirectory;

    rootDirectory= nullptr;

}

// Copy Constructor
//TODO YOU KNOW WHAT TODO
FileSystem::FileSystem(const FileSystem& other):rootDirectory(new Directory(*other.rootDirectory)), workingDirectory(nullptr){ //explanation:
    //we assign nullptr since Weffc++ has warnings regarding initialization list.. we could use the copy assignment here, but copy() method does that and is used in both copy constructor and copy assignment
    //so it might look like poor practice but it's better than code repetition.
    if ((verbose==1) | (verbose==3)){
        cout<<"FileSystem::FileSystem(const FileSystem& other)"<<endl;
    }
    copy(other);
}

//copy for Copy Constructor
void FileSystem::copy(const FileSystem& other){
    string workingDirPath= other.getWorkingDirectory().getAbsolutePath();
    rootDirectory =new Directory (*other.rootDirectory); // *rootDirectory = (other.getRootDirectory());
    Directory* newWorkingDir= findIfLegalPath(workingDirPath);
    setWorkingDirectory(newWorkingDir);

}


//Copy Assignment
FileSystem& FileSystem::operator=(const FileSystem& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"FileSystem& FileSystem::operator=(const FileSystem& other)"<<endl;
    }
    if (this != &other)
    {
        delete rootDirectory;
        copy(other);
    }
    return (*this);
}
// Move Constructor
FileSystem::FileSystem(FileSystem&& other): rootDirectory(other.rootDirectory), workingDirectory(other.workingDirectory){
    if ((verbose==1) | (verbose==3)){
        cout<<"FileSystem::FileSystem(FileSystem&& other)"<<endl;
    }
    other.workingDirectory= nullptr;
    other.rootDirectory= nullptr;
}


//Move Assignment
FileSystem& FileSystem:: operator=(FileSystem&& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"FileSystem& FileSystem:: operator=(FileSystem&& other)"<<endl;
    }
    if (this != &other){ //redundant
        delete rootDirectory;
        setWorkingDirectory(other.workingDirectory);
        rootDirectory = other.rootDirectory;
        other.rootDirectory= nullptr;
        other.workingDirectory= nullptr;
    }
    return (*this);
}
