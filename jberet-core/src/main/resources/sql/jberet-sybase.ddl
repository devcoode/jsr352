CREATE TABLE JOB_INSTANCE (
  JOBINSTANCEID   BIGINT IDENTITY PRIMARY KEY,
  VERSION         INTEGER,
  JOBNAME         VARCHAR(512),
  APPLICATIONNAME VARCHAR(512)
)!!
CREATE TABLE JOB_EXECUTION (
  JOBEXECUTIONID  BIGINT IDENTITY PRIMARY KEY,
  JOBINSTANCEID   BIGINT NOT NULL,
  VERSION         INTEGER,
  CREATETIME      DATETIME,
  STARTTIME       DATETIME,
  ENDTIME         DATETIME,
  LASTUPDATEDTIME DATETIME,
  BATCHSTATUS     VARCHAR(30),
  EXITSTATUS      VARCHAR(512),
  JOBPARAMETERS   VARCHAR(3000),
  RESTARTPOSITION VARCHAR(255),
  CONSTRAINT FK_JOB_EXECUTION_JOB_INSTANCE FOREIGN KEY (JOBINSTANCEID) REFERENCES JOB_INSTANCE (JOBINSTANCEID)
)!!
CREATE TABLE STEP_EXECUTION (
  STEPEXECUTIONID    BIGINT IDENTITY PRIMARY KEY,
  JOBEXECUTIONID     BIGINT NOT NULL,
  VERSION            INTEGER,
  STEPNAME           VARCHAR(255),
  STARTTIME          DATETIME,
  ENDTIME            DATETIME,
  BATCHSTATUS        VARCHAR(30),
  EXITSTATUS         VARCHAR(512),
  EXECUTIONEXCEPTION VARCHAR(2048),
  PERSISTENTUSERDATA VARBINARY(16384),
  READCOUNT          INTEGER,
  WRITECOUNT         INTEGER,
  COMMITCOUNT        INTEGER,
  ROLLBACKCOUNT      INTEGER,
  READSKIPCOUNT      INTEGER,
  PROCESSSKIPCOUNT   INTEGER,
  FILTERCOUNT        INTEGER,
  WRITESKIPCOUNT     INTEGER,
  READERCHECKPOINTINFO  VARBINARY(16384),
  WRITERCHECKPOINTINFO  VARBINARY(16384),
  CONSTRAINT FK_STEP_EXE_JOB_EXE FOREIGN KEY (JOBEXECUTIONID) REFERENCES JOB_EXECUTION (JOBEXECUTIONID)
)!!
CREATE TABLE PARTITION_EXECUTION (
  PARTITIONEXECUTIONID  INTEGER NOT NULL,
  STEPEXECUTIONID       BIGINT  NOT NULL,
  VERSION               INTEGER,
  BATCHSTATUS           VARCHAR(30),
  EXITSTATUS            VARCHAR(512),
  EXECUTIONEXCEPTION    VARCHAR(2048),
  PERSISTENTUSERDATA    VARBINARY(16384),
  READERCHECKPOINTINFO  VARBINARY(16384),
  WRITERCHECKPOINTINFO  VARBINARY(16384),
  PRIMARY KEY (PARTITIONEXECUTIONID, STEPEXECUTIONID),
  CONSTRAINT FK_PARTITION_EXE_STEP_EXE FOREIGN KEY (STEPEXECUTIONID) REFERENCES STEP_EXECUTION (STEPEXECUTIONID)
)!!

