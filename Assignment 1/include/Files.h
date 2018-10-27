#ifndef FILES_H_
#define FILES_H_
 
using namespace std;
#include <string>
#include <vector>
#include "GlobalVariables.h"


class BaseFile {
private:
	string name;
	
public:
	BaseFile(string name);
	string getName() const;
	void setName(string newName);
	virtual int getSize() = 0;
	virtual  ~BaseFile();
	virtual BaseFile* clone()=0;
	virtual string toString(int spaces=0)=0;

	//Additional methods
	bool isDir(); // check if the baseFile (this) is Directory
	void printMe(); //called by LsCommand to print the file

};

class File : public BaseFile {
private:
	int size;
		
public:
	File(string name, int size); // Constructor
	int getSize(); // Return the size of the file
	virtual File* clone(); //creates a copy of the file
	virtual string toString(int spaces=0); // returns string presentation of the file
	//virtual  ~File(){};

};

class Directory : public BaseFile {
private:
	vector<BaseFile*> children;
	Directory *parent;

public:
	//Required Methods
	Directory(string name, Directory *parent); // Constructor
	Directory *getParent() const; // Return a pointer to the parent of this directory
	void setParent(Directory *newParent); // Change the parent of this directory
	void addFile(BaseFile* file); // Add the file to children
	void removeFile(string name); // Remove the file with the specified name from children
	void removeFile(BaseFile* file); // Remove the file from children
	void sortByName(); // Sort children by name alphabetically (not recursively)
	void sortBySize(); // Sort children by size (not recursively)
	vector<BaseFile*> getChildren(); // Return children
	int getSize(); // Return the size of the directory (recursively)
	string getAbsolutePath();  //Return the path from the root to this

	//Additional Methods
	static bool CompareByName(BaseFile* file1, BaseFile* file2); //Name comparator
	static bool CompareBySize(BaseFile* file1, BaseFile* file2); //Size comparator
	int getSizeRec(int sum); //Recursive getSize
	string getPathRec(); //Recursive getAbsolutePath
    void emptySelf(); //delete all content of children vector
	virtual string toString(int spaces=0); // returns string presentation of the directory
	bool isChild(string dirName); //  boolean check if is directory child
	BaseFile* find(string fileName);  // return the child BaseFile with fileName
    File* findFile(string fileName);  // return the child File with fileName
    Directory* findDir(string dirName);  // return the child Directory with fileName
	void print(); //prints content of dir for LsCommand
    void removeFromChildren(BaseFile* file); //delete the directory/file from directory
    void removeFromChildren(string name); //delete the directory/file from directory by name

    bool isAncestor(Directory* dir); //checks if dir is a parent (ancestor) of this

	//Rule of 5
    virtual ~Directory(); // Destructor
    Directory(const Directory& other); // Copy Constructor
    Directory& operator=(const Directory& other); //Copy Assignment
    Directory(Directory&& other); // Move Constructor
    Directory& operator=(Directory&& other); //Move Assignment

	//Rule of 5 sub-methods
	void copy(const Directory& other);//copy for Copy Constructor
	virtual Directory* clone(); //implement clone

    };

#endif