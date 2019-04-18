# filetests
This project is created for testing file migration from one ditrctory to another one.
Files are migrated from *source.dir* to *dest.dir*. The set of tests includes the following tests:
- file exists
- file modification date isn't changed
- permissions aren't changed
- file owner isn't changed
- symlink exists

Use the following command to run the tests, e.g.:

```
git clone https://github.com/d-kay23/filetests.git && cd filetests && mvn test -Dtest=FileTests -Dsource.dir=/tmp/dir1 -Ddest.dir=/tmp/dir2 -Dparallel=2 -Dthreadcount=2
```

Also, you are able to run Allure report:

```
mvn io.qameta.allure:allure-maven:serve
```
