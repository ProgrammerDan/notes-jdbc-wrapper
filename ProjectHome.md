This is not full featured driver with sql support (yet).

Our first target to retrieve information from VIEWs created on the Lotus Notes through JDBC interface.

Currently, the native notes jdbc driver is not supported anymore. You can still find ODBC driver, and work through JDBC-ODBC bridge, bu we need pure JDBC because our OS is not windows compatible.

## Idea ##
There is a native Lotus Notes API (Notes.jar) that we can use to retrieve data from Lotus views. So we gonna just wrap this interface in jdbc.

## The query to be implemented ##
At first stage there will be a very simple query:
```
SELECT VIEW Data\Lotus View Name
```
where `Data\Lotus View Name` is a lotus view name (could be with path).

You can specify prepared query parameters (will be explaned later)

and this query:
```
SELECT VIEWLIST
```

This one returns all views from the current database.

## Connection ##
```
driver   = org.notes.driver.LNDriver
url      = jdbc:org:notes:LNSERVER:63148?db=App\MyPrj\MyProject.nsf
user     = lotus user name that has access to desired data
password = password
```
Where:
| LNSERVER | ip address or name of the server where lotus notes located.|
|:---------|:-----------------------------------------------------------|
| 63148 | the DIIOP (IIOP) port that must be opened on lotus server (ask your lotus admins). 63148 is default value. |
|`App\MyPrj\MyProject.nsf` | path to lotus nsf database (see database properties of your view)|