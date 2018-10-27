#ifndef FILESYSTEM_H_
#define FILESYSTEM_H_

#include "Files.h"


class FileSystem {
private:
	Directory* rootDirectory;
	Directory* workingDirectory;
public:
	FileSystem();
	Directory& getRootDirectory() const; // Return reference to the root directory
	Directory& getWorkingDirectory() const; // Return reference to the working directory
	void setWorkingDirectory(Directory *newWorkingDirectory); // Change the working directory of the file system

	//Additional Methods
	Directory* findIfLegalPath(string cdPath, bool mkdir=false); //return the dir in the path, if mkdir==true, create the path
	bool setWorkingDirIfLegalPath(string path); // Change the working directory of the file system if path is legal
	bool isLegalPath(string path);

	//Rule of 5
	virtual ~FileSystem(); // Destructor
	FileSystem(const FileSystem& other); // Copy Constructor
	FileSystem& operator=(const FileSystem& other); //Copy Assignment
	FileSystem(FileSystem&& other); // Move Constructor
	FileSystem& operator=(FileSystem&& other); //Move Assignment

	//help rule of 5
	void copy(const FileSystem& other);//copy for Copy Constructor
};


#endif
