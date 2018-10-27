
using namespace std;
#include "../include/Files.h"
#include <algorithm> //remove_if
#include <typeinfo>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

//BaseFile constructor
BaseFile::BaseFile(string name): name(name){}

string BaseFile::getName() const
{
    return name;
}

void BaseFile::setName(string newName)
{
    name = newName;
}
BaseFile::~BaseFile(){} // Destructor

// check if baseFile is Directory
bool BaseFile:: isDir (){
    return (typeid(*this)== typeid(Directory));
}

//used to print via LsCommand
void BaseFile::printMe(){
    string type;
    if (isDir()){
        type="DIR";
    }
    else{
        type="FILE";
    }
    cout<< type <<'\t' << getName()<<'\t' << getSize() << endl;
}
//////////
///File///
//////////
//Constructor
File::File(string name, int size) : BaseFile(name), size(size){}

int File::getSize()
{
    return size;
}

File* File:: clone(){
    return new File(getName(), size); //default constructor works, redundant.
}
//Additional Methods

// returns string presentation of the file
string File::toString(int spaces){
    std::stringstream ss;
    const void* address= static_cast<const void*>(this);
    ss << getName() << "\t[" << address <<"]" <<"\n"; // append address and newline
    string str= ss.str();
    return str;
}

///////////////
///Directory///
///////////////

//Dir Constructor

Directory::Directory(string name, Directory *parent):BaseFile(name), children(), parent(parent){}

//RULE OF 5 (resource fields in class)
Directory::~Directory() // Destructor
{
    if ((verbose==1) | (verbose==3)){
        cout<<"Directory::~Directory()"<<endl;
    }
    emptySelf(); //delete children
    setParent(nullptr);
}

Directory::Directory(const Directory& other): BaseFile(other.getName()), children(), parent(other.getParent()) // Copy Constructor
{
    if ((verbose==1) | (verbose==3)){
        cout<<"Directory::Directory(const Directory& other)"<<endl;
    }
    copy(other);
}

Directory* Directory::clone()
{
    return new Directory(*this);
}

void Directory::copy(const Directory& other) {
    setName(other.getName()); //redundant in copy constructor
    for(unsigned int i=0; i< other.children.size(); i++) {
        addFile((other.children.at(i))->clone()); //adds to children vector and updates parent to be this if file is a directory
    }
}
//Copy Assignment
Directory& Directory:: operator=(const Directory& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"Directory& Directory:: operator=(const Directory& other)"<<endl;
    }
    if (this != &other)
    {
            emptySelf();
            copy(other);
    }
    return (*this);
}
// Move Constructor
Directory::Directory(Directory &&other):BaseFile(other.getName()), children(move(other.children)), parent(other.parent){
    if ((verbose==1) | (verbose==3)){
        cout<<"Directory::Directory(Directory &&other)"<<endl;
    }
    //updates children's parent to be this
    for (unsigned int i=0; i<children.size(); i++){
        if(children.at(i)->isDir()) {
            ((Directory *) children.at(i))->setParent(this);
        }
    }
    if(parent!= nullptr){
        (other.parent)->removeFromChildren(other.getName()); //remove other from parent's children
        parent->addFile(this); //add the new child
    }
    other.emptySelf(); // call clear?
    other.parent= nullptr;
}
//Move Assignment
Directory& Directory::operator=(Directory&& other){
    if ((verbose==1) | (verbose==3)){
        cout<<"Directory& Directory::operator=(Directory&& other)"<<endl;
    }
    if (this != &other){
        emptySelf(); //empty children vector;
        if (parent != nullptr){
            parent->removeFromChildren(this);
        }
        if ((other.parent)!= nullptr)
        {
            (other.parent)->removeFromChildren(&other);
            other.parent->addFile(this);
        }
        children=move(other.children);
        for(unsigned int i=0; i<children.size(); i++){
            if(children.at(i)->isDir()) {
                ((Directory *) children.at(i))->setParent(this);
            }
        }
        other.children.clear(); //should already be empty becuse we called move
        setName(other.getName());

        setParent(other.parent);
        other.parent= nullptr;
    }
    return (*this);
}

//CLASS FUNCTIONS
Directory* Directory::getParent() const
{
    return parent;
}

void Directory::setParent(Directory* newParent)
{
    parent= newParent;
}

void Directory::addFile(BaseFile* file)
{
    children.push_back(file);
    if(typeid(*file)== typeid(Directory)){
        ((Directory*)file)->setParent(this);
    }
}

//remove all the elements in children vector
void Directory::emptySelf(){
    while(!children.empty()){
       removeFile(*children.begin());
    }
}
void Directory::removeFile(string name)
{
    vector<BaseFile*>::iterator toRemoveIt;
    //finds the first BaseFile* in children who's name is name
    toRemoveIt = find_if (children.begin(), children.end(), [name](BaseFile* file){ return (file->getName()==name); });
    removeFile(*toRemoveIt);
}
void Directory::removeFromChildren(string name)
{
    vector<BaseFile*>::iterator toRemoveIt;
    //finds the first BaseFile* in children who's name is name
    toRemoveIt = find_if (children.begin(), children.end(), [name](BaseFile* file){ return (file->getName()==name); });
    removeFromChildren(*toRemoveIt);
}
void Directory::removeFromChildren(BaseFile* file){
    children.erase(std::remove(children.begin(), children.end(), file), children.end()); //deletes the pointer to file from vector
}
//checked1
void Directory::removeFile(BaseFile* file)
{
    removeFromChildren(file);
    delete file;
}

bool Directory::CompareByName(BaseFile* file1, BaseFile* file2)
{
    return (file1->getName().compare(file2->getName())<0);
}

void Directory::sortByName()
{
    std::sort(children.begin(), children.end(), CompareByName);
}

bool Directory::CompareBySize(BaseFile* file1, BaseFile* file2)
{
    if ((file1->getSize()) == (file2->getSize())){  //if same size, sort by name like in the instructions
        return CompareByName (file1, file2);
    }
    return ((file1->getSize()) < (file2->getSize()));
}

void Directory::sortBySize()
{
    std::sort(children.begin(), children.end(), CompareBySize);
}

vector<BaseFile*> Directory::getChildren() {
    return children;
}

Directory* Directory::findDir(string dirName){
    BaseFile* file=find(dirName);
    if(file!= nullptr && file->isDir()){
        return (Directory*)file;
    }
    else{
        return nullptr;
    }
}
File* Directory::findFile(string fileName){
    BaseFile* file=find(fileName);
    if(file!= nullptr && !(file->isDir())){
        return (File*)file;
    }
    else{
        return nullptr;
    }
}

BaseFile* Directory::find(string fileName){
    if(fileName==".."){
        return parent;
    }
    bool found=false;
    BaseFile* file= nullptr;
    vector<BaseFile*>::iterator it;
    for(it=children.begin(); (it!=children.end()) & (!found); ++it)
    {
        if((*it)->getName()==fileName){
            found=true;
            file=(*it);
        }
    }
    return file;
}

bool Directory::isChild(string childName){
    return (find(childName)!=nullptr);
}
int Directory::getSizeRec(int sum)
{

    std::vector<BaseFile*>::iterator it;
    for(it=children.begin(); it != children.end(); ++it) {
        sum = sum + (*it)->getSize();
    }
    return sum;
}

int Directory::getSize()
{
    return getSizeRec(0);
}

//recursive method for getAbsolutePath
string Directory::getPathRec()
{
    if(parent==nullptr){
        return "";
    }

    return parent->getPathRec()+ "/"+ getName();
}

string Directory::getAbsolutePath()
{
    string path=getPathRec();
    if(path==""){
        path="/";
    }
    return path;
}
// returns string presentation of the file
string Directory::toString(int spaces){
    std::stringstream ss;
    const void* address= static_cast<const void*>(this);
    ss << getName() << "\t[" << address <<"]" <<"\n"; // append address and newline
    string str= ss.str();
    for(unsigned int i=0; i<children.size(); i++) {
        str.insert(str.end(), spaces+1, '\t');
        str += children.at(i)->toString(spaces+1);
    }

    return str;
}

void Directory::print(){
    for_each(children.begin(), children.end(),[](BaseFile* file){ file->printMe();});
}

//checks if toCheck is a parent of this or it's parents
bool Directory::isAncestor(Directory* toCheck){
    Directory* currParent= getParent();

    bool isParent=false; //if this is root directory, return false

    while(!isParent && currParent!=nullptr){
        if(currParent==toCheck){
            isParent=true;
        }
        currParent=currParent->getParent();
    }
    return isParent;
}
