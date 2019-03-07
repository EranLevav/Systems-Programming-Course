#ifndef COMMANDS_H_
#define COMMANDS_H_

#include <string>
#include "FileSystem.h"
#include "GlobalVariables.h"



class BaseCommand {
private:
	string args;

public:
	BaseCommand(string args);
	string getArgs();
	virtual void execute(FileSystem & fs) = 0;
	virtual string toString() = 0;
	//additional methods;
	static string trimSpaces(string str);
    static string getStartOfPath(string path); // divide the path to two parts
    static string getEndOfPath(string path);// divide the path to two parts
    static string getFirstArg(string str); //return first argument of string (before the space)
    static string getSecondArg(string str); //return 2nd argument of string (after the space)
    virtual BaseCommand* copyCommand()=0; // help method in rule of 5 for environment
    virtual ~BaseCommand()=default;//distructor

};

class PwdCommand : public BaseCommand {
private:
public:
	PwdCommand(string args);
	void execute(FileSystem & fs); // Every derived class should implement this function according to the document (pdf)
	virtual string toString();
    virtual PwdCommand* copyCommand ();
    virtual ~PwdCommand()=default;//distructor


};

class CdCommand : public BaseCommand {
private:
public:
	CdCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual CdCommand* copyCommand ();
    virtual ~CdCommand()=default;//distructor

    };

class LsCommand : public BaseCommand {
private:
public:
	LsCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual LsCommand* copyCommand ();
    virtual ~LsCommand()=default;//distructor

};

class MkdirCommand : public BaseCommand {
private:
public:
	MkdirCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual MkdirCommand* copyCommand ();
    virtual ~MkdirCommand()=default;//distructor

};

class MkfileCommand : public BaseCommand {
private:
public:
	MkfileCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual MkfileCommand* copyCommand ();
    virtual ~MkfileCommand()=default;//distructor

};

class CpCommand : public BaseCommand {
private:
public:
	CpCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual CpCommand* copyCommand ();
    virtual ~CpCommand()=default;//distructor

};

class MvCommand : public BaseCommand {
private:
public:
	MvCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual MvCommand* copyCommand ();
    virtual ~MvCommand()=default;//distructor


};

class RenameCommand : public BaseCommand {
private:
public:
	RenameCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual RenameCommand* copyCommand ();
    virtual ~RenameCommand()=default;//distructor


};

class RmCommand : public BaseCommand {
private:
public:
	RmCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual RmCommand* copyCommand ();
    virtual ~RmCommand()=default;//distructor

};

class HistoryCommand : public BaseCommand {
private:
	const vector<BaseCommand *> & history;
public:
	HistoryCommand(string args, const vector<BaseCommand *> & history);
	void execute(FileSystem & fs);
	string toString();
    virtual HistoryCommand* copyCommand ();
    virtual ~HistoryCommand()=default;//distructor

};


class VerboseCommand : public BaseCommand {
private:
public:
	VerboseCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual VerboseCommand* copyCommand ();
    virtual ~VerboseCommand()=default;//distructor

};

class ErrorCommand : public BaseCommand {
private:
public:
	ErrorCommand(string args);
	void execute(FileSystem & fs);
	string toString();
    virtual ErrorCommand* copyCommand ();
    virtual ~ErrorCommand()=default;//distructor

};

class ExecCommand : public BaseCommand {
private:
	const vector<BaseCommand *> & history; //not responsible for object, only reference
public:
	ExecCommand(string args, const vector<BaseCommand *> & history);
	void execute(FileSystem & fs);
	string toString();
    virtual ExecCommand* copyCommand ();
    virtual ~ExecCommand()= default;//distructor

};


#endif
