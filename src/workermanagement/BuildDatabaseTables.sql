create table "ROOT".UNIT
(
	ID INTEGER generated always as identity not null primary key,
	NAME VARCHAR(30) not null
);


create table "ROOT".WORKER
(
	ID INTEGER generated always as identity not null primary key,
	FIRSTNAME VARCHAR(30) not null,
	LASTNAME VARCHAR(30) not null,
	PHONE VARCHAR(13),
	CURRENTRATE DECIMAL(5,2) not null
);

create table "ROOT".WORKITEM
(
	ID INTEGER generated always as identity not null primary key,
	DATE DATE not null,
	RATE DECIMAL(5,2) not null,
	QTY DECIMAL(28,2) not null,
	WORKERID INTEGER not null,
	UNITID INTEGER not null
);


create table "ROOT".USERS
(
	ID INTEGER generated always as identity not null primary key,
	USERNAME VARCHAR(30) not null unique,
	PASSWORD VARCHAR(64) not null,
        CHALLENGEQUESTION VARCHAR(255) not null,
        CHALLENGEANSWER VARCHAR(64) not null
);

ALTER TABLE WORKER ALTER ID SET INCREMENT BY 1;
ALTER TABLE UNIT ALTER ID SET INCREMENT BY 1;
ALTER TABLE WORKITEM ALTER ID SET INCREMENT BY 1;
ALTER TABLE USERS ALTER ID SET INCREMENT BY 1;

ALTER TABLE WORKITEM
ADD FOREIGN KEY (WORKERID)
REFERENCES WORKER (ID);

ALTER TABLE WORKITEM
ADD FOREIGN KEY (UNITID)
REFERENCES UNIT (ID);
